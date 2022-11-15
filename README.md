# BibCLI

A simple tool to manage your bibliography resources locally.

## Features

* Central repository for managing all ressources
* Metadata storage in Bib-Tex format
+ project specific reference management
* Selective Bib-Tex export
* Git integration

## How?

BibCLI will create a central repository of all recources in `~/.bibcli`.
It will store all ressources and bibtex files with metadata.

A specific project refers to ressources in your central repository with `bib-ref`.
BibCLI can generate a project specific bibtex file form the `bib-ref`.

## Prerequisites

Git is set up correctly.

## Usage

### Get started

```bash
bibcli init --central --git
```

> Your repository will be created in `~/.bibcli`

(Origin can be added manually with `git origin set <origin>`)

### Git automated commits

```bash
bibcli config autocommit on
```

### Git automated push

```bash
bibcli config autopush on
```

### Add resources

```bash
bibcli add <path>
```

```bash
bibcli add <path> --bibtex <path> --commit --push
```

```bash
bibcli add <path> --alias <alias name> --type article --commit --push
```

> Alias or name is required for all ressources

```bash
bibcli move <path> --bibtex <path> --commit --push
```

### Add configuration to a project
```bash
bibcli init --ressources <alias 1> <alias 2> <alias 3>
```

### List all ressources

```bash
bibcli list
```

### Find ressources

```bash
bibcli list author "Einstein"
```

### Add resources to a project

```bash
bibcli add <alias name>
```

```bash
bibcli add --author "Einstein" --all
```

### Remove

```bash
bibcli rm <alias name>
```

### Generate a .bib file from a file

```bash
bibcli generate --out out.bib
```

### Get path

```bash
bibcli path <alias name>
```

## ~/.bibcli

```
/.bibcli
|-.git
|-/res
  |-/alias-name
    |-/assets
    |-<resource>
    |-bib
    |-meta.json
|-config
```

### ~/.bibcli/config

```conf
autocommit = "false"
autopush = "false"
```

### project/bib-ref

```
<alias 1>
<alias 2>
<alias 3>
```

### ~/.bibcli/alias-name/meta.json

* contains only personal tagging information yet

```json
"tags": ["disagree", "wrong"]
```
