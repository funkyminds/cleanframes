# Script based on: https://github.com/holdenk/spark-testing-base/blob/master/mini-cross-build.pl

#!/usr/bin/perl

use File::Slurp;
use strict;
use warnings;

my @spark_versions = ("2.0.0", "2.0.1", "2.0.2", "2.1.0", "2.1.1", "2.1.2", "2.1.3", "2.2.0", "2.2.1", "2.2.2", "2.3.0", "2.3.1", "2.3.2", "2.4.0");

# Backup the build file
`cp build.sbt build.sbt_back`;

# Get the original version
my $input = read_file("build.sbt");

my $original_version;

if ($input =~ /version\s+\:\=\s+\"(.+?)\"/) {
    $original_version = $1;
}
else {
    die "Could not extract version";
}

print "Building original version - $original_version";

print `./sbt/sbt clean`;
print "Cross building for $original_version";

foreach my $spark_version (@spark_versions) {
    my $target_version = $spark_version."_".$original_version;
    print "New target version ".$target_version;
    print "\n"
}