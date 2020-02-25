# Raktr [![Build Status](https://api.travis-ci.org/mboldi/Raktr.svg?branch=master)](https://travis-ci.org/mboldi/Raktr)

This is an inventory and rent manager for a university studio.

### Database

Install PostgreSQL and
* set its port to the default `5432`
* set password to `admin`
* create database with the name `raktrdb`.

### Simple database with docker

```
docker run -e POSTGRES_PASSWORD=admin -e POSTGRES_DB=raktrdb -p 5432:5432 -d postgres:alpine
```