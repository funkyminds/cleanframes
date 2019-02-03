# Script based on: https://github.com/holdenk/spark-testing-base/blob/master/mini-cross-build.pl

#!/usr/bin/perl

use File::Slurp;
use strict;
use warnings;

my @spark_versions = ("2.1.0");
# ("2.0.0", "2.0.1", "2.0.2", "2.1.0", "2.1.1", "2.1.2", "2.1.3", "2.2.0", "2.2.1", "2.2.2", "2.3.0", "2.3.1", "2.3.2", "2.4.0");

# Backup the build file
`cp build.sbt build.sbt_back`;

# Get the original version
my $input = read_file("build.sbt");

my $original_version;

if ($input =~ /\s+lazy\s+val\s+projectVersion\s+\=\s+\"(.+?)\"/) {
    $original_version = $1;
} else {
    die "Could not extract version";
}

print "Building original version - $original_version";

# print `./sbt/sbt clean`;
print "Cross building for $original_version";

foreach my $spark_version (@spark_versions) {
    my $target_version = $spark_version."_".$original_version;
    print "New target version ".$target_version;
    my $new_build = $input;
    $new_build =~ s/\s+lazy\s+val\s+projectVersion\s+\=\s+\".+?\"/lazy val projectVersion = "$target_version"/;
    $new_build =~ s/\s+lazy\s+val\s+sparkVersion\s+\=\s+\".+?\"/\nlazy val sparkVersion = "$spark_version"\n/;
    print `git branch -d release-v$target_version`;
    print `git checkout -b release-v$target_version`;
    print "new build file $new_build hit";
    open (OUT, ">build.sbt");
    print OUT $new_build;
    close (OUT);
    print `git commit -am "Make release for $target_version"`;
    print `git push -f --set-upstream origin release-v$target_version`;
    print "building";
    # more more more
    # TODO: change branch to master
    print "switch back to feature/cross-build-deployment-script";
    print `git checkout feature/cross-build-deployment-script`;
    print "built"
}
print "Press enter once published to maven central";
my $j = <>;

foreach my $spark_version (@spark_versions) {
    my $target_version = $spark_version."_".$original_version;
    print "Publishing new target version ".$target_version;
    my $new_build = $input;
    $new_build =~ s/\s+lazy\s+val\s+projectVersion\s+\=\s+\".+?\"/lazy val projectVersion = "$target_version"/;
    $new_build =~ s/\s+lazy\s+val\s+sparkVersion\s+\=\s+\".+?\"/\nlazy val sparkVersion = "$spark_version"/;
    print `git checkout -b release-v$target_version`;
    print "new build file $new_build hit";
    open (OUT, ">build.sbt");
    print OUT $new_build;
    close (OUT);
    print "publishing";
    # more more more
}

`cp build.sbt_back build.sbt`;
