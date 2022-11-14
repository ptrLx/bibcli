# BibCLI

A simple tool to manage your bibliography resources locally.

## Features

* Central and derived repository and configurations
* Ressource management
* Metadata storage in Bib-Tex format
* Selective Bib-Tex export
* Git integration

## Prerequisites

Git is set up correctly.

## Usage

Get started:

```bash
bibcli init --central --git
```

> Repository will be created in `~/.bibcli`

(Origin can be added manually with `git origin set <origin>`)

Git automated commits:

```bash
bibcli config --central autocommit on
```

Git automated push:

```bash
bibcli config --central autopush on
```

Add resources:

```bash
bibcli add <path> --bibtex <path> --central --commit --push
```

```bash
bibcli add <path> --alias <alias name> --type article --central --commit --push
```

> Alias or name is required for all ressources

```bash
bibcli move <path> --bibtex <path> --central --commit --push
```

Add configuration to a project:

```bash
bibcli init --git
```

Search ressources:

```bash
bibcli search author "Einstein" --central

> add as derivative to this repository? [N/y]
```

Add derived resources to a project:

```bash
bibcli add --copy --from-central <alias name>
```

```bash
bibcli add --from-central --author "Einstein" --all
```

Remove:

```
bibcli rm <alias name>
```

Generate a .bib file for project:

```bash
bibcli generate bib/out.bib
```

List:

```bash
bibcli list
```

Path:

```bash
bibcli path <alias name>
```

## File structure

bibcli requires a `bib` folder for repository configuration.

```
bib
|-res
|-alias-name
  |-<resource>
  |-.bib
|-bib.json
|-out.bib
```

`bib.json`:
* connects bibtex metadata in `.bib` with your real files
* can extend your bibliography with other types (videos, links)
* contains personal tagging information
* contains references to central repository
* contains configuration (autocommit, autopush, ...)

example:

```json
{
  "resources": {
      "<alias-name>": {
      "type": "reference",
    },
    "<alias-name>": {
      "type": "bibtex",
      "tags": ["disagree", "wrong"]
    },
    "<alias-name>": {
      "type": "video",
      "tags": ["skip to 3:30", "speed 2x"]
    }
  },
  "autocommit": false,
  "autopush": false
}
```
