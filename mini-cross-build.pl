# Script based on: https://github.com/holdenk/spark-testing-base/blob/master/mini-cross-build.pl

#!/usr/bin/perl

use strict;
use warnings;

my @spark_versions = (
    "1.6.0", "1.6.1", "1.6.2", "1.6.3",
    "2.0.0", "2.0.1", "2.0.2", "2.1.0",
    "2.1.1", "2.1.2", "2.1.3",
    "2.2.0", "2.2.1", "2.2.2", "2.2.3",
    "2.3.0", "2.3.1", "2.3.2", "2.3.3",
    "2.4.0", "2.4.1", "2.4.2", "2.4.3");

foreach my $spark_version (@spark_versions) {
    print "Next spark version ".$spark_version;
    print "\nbuilding\n";
    print "\nGoing to run: ./sbt/sbt -DsparkVersion=$spark_version clean +publishSigned\n";
    print `./sbt/sbt -DsparkVersion=$spark_version clean +publishSigned`;
    print "\nbuilt\n";
}