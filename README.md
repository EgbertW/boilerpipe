boilerpipe-ng
==========

Boilerplate Removal and Fulltext Extraction from HTML pages

This is a fork of the excellent yet abandoned boilerpipe by Dr. Christan Kohlschütter available at https://github.com/kohlschutter/boilerpipe

It was forked on the additions done by [dankito](https://github.com/dankito/boilerpipe).

Main difference from these repositories:
- Remove included nekohtml and depend on Maven resource instead
- Apply for inclusion in JCenter
- Name change to avoid namespace conflicts

To use, make sure you enabled JCenter as source and add a dependency on BoilerpipeNG and on XercesImpl:

Maven:

```
<dependency>
    <groupId>xerces</groupId>
    <artifactId>xercesImpl</artifactId>
    <version>2.12.0</version>
</dependency>
<dependency>
    <groupId>nl.pointpro</groupId>
    <artifactId>boilerpipeng</artifactId>
    <version>2.0.0</version>
    <type>pom</type>
</dependency>
```

Gradle:
```
compile 'nl.pointpro:boilerpipeng:2.0.0'
compile 'xerces.xercesImpl:2.12.0'
```