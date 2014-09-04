#!/bin/bash

mvn -DaltDeploymentRepository=github-repo-releases::default::file:./releases clean deploy
