[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/zg2pro/regex-copy-plugin)
[![Build](https://travis-ci.org/zg2pro/regex-copy-plugin.svg?branch=master)](https://travis-ci.org/zg2pro/regex-copy-plugin)
[![BCH compliance](https://bettercodehub.com/edge/badge/zg2pro/regex-copy-plugin?branch=master)](https://bettercodehub.com/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.zg2pro.copy/regex-copy-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.zg2pro.copy/regex-copy-plugin)


# regex-copy-plugin

forked from ggtools/regex-copy-plugin

## Description

The maven resource plugins lacks the possibility of renaming the resources during the copy process. This plugin aims at fixing this by providing a versatile renaming system. In a nutshell a regular expression will be used to match the source resources and the groups from this regular expression will be used to build the destination.

## Configuration

```xml
<plugin>
    <artifactId>regex-copy-maven-plugin</artifactId>
    <configuration>
        <sourceDirectory>src/main/java</sourceDirectory>
        <destinationDirectory>target/test-harness/regex-copy</destinationDirectory>
        <source>(net/ggtools/maven)/(.+)\.java</source>
        <destination>{2}/Test.java</destination>
    </configuration>
</plugin>
```
