# BibCLI

A simple tool to manage your bibliography resources locally.

## Features

* Central repository for managing all resources
* Metadata storage in Bib-Tex format
* project specific reference management
* Selective Bib-Tex export
* Git integration

## How?

BibCLI will create a central repository of all resources in `~/.bibcli`.
It will store all resources and bibtex files with metadata.

A specific project refers to resources in your central repository with `bib-ref`.
BibCLI can generate a project specific bibtex file form the `bib-ref`.

## Prerequisites

Git is set up correctly.

## Installation

### Babashka

* Install babashka: `bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install)`
* Execute with

  ```bash
  bb -m bibcli.main <args>
  ```

## Usage

### Get started

```bash
bibcli initc --git
```

> Your repository will be created in `~/.bibcli`

(Origin can be added manually with `git origin set <origin>`)

### Git automated commits

```bash
bibcli config --autocommit
```

```bash
bibcli config --no-autocommit
```

### Git automated push

```bash
bibcli config --autopush
```

```bash
bibcli config --no-autopush
```

### Add resources

```bash
bibcli addc <path>
```

```bash
bibcli addc <path> --bibtex <path> --commit --push
```

```bash
bibcli addc <path> --alias <alias name> --type article --commit --push
```

> Alias or name is required for all resources

```bash
bibcli movec <path> --bibtex <path> --commit --push
```

### Add configuration to a project

```bash
bibcli init --resources <alias 1> <alias 2> <alias 3>
```

### List all resources

```bash
bibcli list
```

### Find resources

```bash
bibcli list author "Einstein"
```

### Add resources to a project

```bash
bibcli add <alias list>
```

```bash
bibcli add --author "Einstein"
```

### Remove

```bash
bibcli rm <alias list>
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

### ~/.bibcli/alias-name/tags

contains personal tagging information

```
disagree
wrong
```

## Dev environment

Required:

* Docker
* VS Code

Get started:

* Install the remote-containers extension
* Start the dev container
* Start Calva Repl in VS Code
