#!/bin/sh
# PIPE2 release package builder

# This script is meant to be used in a directory containing a full CVS checkout
# of the PIPE2 project.

# If the script is invoked with an incorrect number of arguments, then an explaination
# of the usage is given and the script exits.
if [ $# -ne 1 ]  
then
  echo "Incorrect number of arguments."
  echo "Usage: `basename $0` [release version number]"
  echo "e.g.  ./`basename $0` 2.5"
  exit 65
fi

# All decimal points in the version number are replaced with dashes, and this is used
# to create the filename for the .zip package.
version_number=$1
version_number=${version_number/\./-}
package_filename="PipeV"$version_number

# Compiles PIPE2 to build/app/ using ant.
ant || {
  echo "Cannot compile PIPE2 using Ant."
  exit;
}

# Copies binaries to bin/ directory.
mkdir -p bin || {
  echo "Error creating bin/ directory."
  exit;
}
cp -r build/app/* bin || {
  echo "Error copying PIPE2 build to bin/."
  exit;
}

# Creates javadoc in docs/api/
javadoc -d docs/api/ -sourcepath src/ -subpackages pipe || {
  echo "Cannot create javadoc."
  exit;
}

# Creates release package from the contents of the bin, docs, Resources, src, test directories
# (ignoring directories named 'CVS') and the 2 readme files.
find bin docs Resources src test -type d -not -name "*CVS*" -exec zip -r $package_filename {} ';' || {
  echo "Cannot create PIPE2 release .zip package."
  exit;
}
zip -r $package_filename readme.dnamaca.txt readme.txt || {
  echo "Cannot add readme files to PIPE2 release .zip package."
  exit;
}

exit 0