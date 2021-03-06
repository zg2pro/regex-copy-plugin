package net.ggtools.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 */
@Mojo(name = "regex-copy")
public class RegexCopyMojo extends AbstractMojo {
    private static final CopyOption[] COPY_OPTIONS_WITHOUT_OVERWRITE = new CopyOption[] {
        StandardCopyOption.COPY_ATTRIBUTES
    };
    private static final CopyOption[] COPY_OPTIONS_WITH_OVERWRITE = new CopyOption[] {
        StandardCopyOption.COPY_ATTRIBUTES,
        StandardCopyOption.REPLACE_EXISTING
    };

    @Parameter(defaultValue = "${project.basedir}")
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}")
    private File destinationDirectory;

    @Parameter(property = "regex-copy.source", required = true)
    private String source;

    @Parameter(property = "regex-copy.destination", required = true)
    private String destination;

    @Parameter(property = "regex-copy.overwrite", defaultValue = "false")
    private boolean overwrite;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Performing regex-copy");
        getLog().debug("Getting candidate files and directories");
        String firstSource = source;
        int firstIndexOfSlash = firstSource.indexOf("/");
        if (firstIndexOfSlash > -1) {
            firstSource = firstSource.substring(0, firstIndexOfSlash);
        }
        run(sourceDirectory.toPath(), firstSource);
    }

    private void run(Path rootPath, String curSource)
        throws MojoExecutionException {
        try {
            List<RegexFileScanner.Result> results = RegexFileScanner.scan(
                rootPath,
                curSource
            );
            getLog().info("Got " + results.size() + " files or directories");
            getLog().debug("Results: " + results);
            for (RegexFileScanner.Result result : results) {
                copyResult(result, curSource);
            }
        } catch (IOException e) {
            String msg = "Error while retrieving candidate files";
            getLog().error(msg, e);
            throw new MojoExecutionException(msg, e);
        }
    }

    private Path mapResultToDestination(RegexFileScanner.Result result) {
        String resultDest = destination;
        //String fileAbsolutePath = result.getPath().toString();
        //String fileRelativePath = fileAbsolutePath.substring(fileAbsolutePath.indexOf(sourceDirectory.));

        Pattern patt = Pattern.compile(
            sourceDirectory.getAbsolutePath().replaceAll("\\\\", "/") +
            "/" +
            source
        );
        Matcher m = patt.matcher(
            result.getPath().toString().replaceAll("\\\\", "\\/")
        );
        if (m.matches()) {
            for (int i = 1; i < m.toMatchResult().groupCount() + 1; i++) {
                String group = m.group(i);
                if (group != null) {
                    resultDest =
                        resultDest.replaceAll("\\{" + i + "\\}", group);
                }
            }
            return destinationDirectory.toPath().resolve(resultDest);
        }
        return null;
    }

    private void copyResult(RegexFileScanner.Result result, String curSource)
        throws MojoExecutionException, IOException {
        if (Files.isDirectory(result.getPath())) {
            int lastIndexOfCurSource = source.lastIndexOf(curSource);
            String remainingPath = source.substring(
                lastIndexOfCurSource + curSource.length() + 1,
                source.length()
            );
            int firstIndexOfSlash = remainingPath.indexOf("/");
            if (firstIndexOfSlash > -1) {
                remainingPath = remainingPath.substring(0, firstIndexOfSlash);
            }
            run(result.getPath(), remainingPath);
            return;
        } else if (!Files.isRegularFile(result.getPath())) {
            getLog()
                .warn(
                    "Only files are accepted at the moment skipping " +
                    result.getPath()
                );
            return;
        }

        Path destPath = mapResultToDestination(result);
        if (destPath == null) {
            return;
        }
        destPath = destPath.toAbsolutePath();
        if (!Files.exists(destPath.getParent())) {
            Files.createDirectories(destPath.getParent());
        }

        try {
            Files.copy(
                result.getPath(),
                destPath,
                overwrite
                    ? COPY_OPTIONS_WITH_OVERWRITE
                    : COPY_OPTIONS_WITHOUT_OVERWRITE
            );
        } catch (FileAlreadyExistsException e) {
            getLog().warn("File " + destPath + " already exists, skipping");
            getLog().debug(e);
        }
    }
}
