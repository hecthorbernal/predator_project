#!/usr/bin/perl
use strict;
use warnings;

open my $file,"<", "OffensiveProfaneWordList.txt" or die $!;
open my $output,">", "OffensiveProfaneWordList_with_spaces.txt" or die $!;

while(<$file>){
	my $line = $_;
	$line =~ s/(\w)/$1\\\\s/g;
	$line =~ s/\\\\s$/\.\*"/g;
	$line =~ s/^/"\.\*/;	
	print $output $line;
}
close $file;
close $output;

