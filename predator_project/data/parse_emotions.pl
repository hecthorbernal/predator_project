#!/usr/bin/perl
use strict;
use warnings;

my $n_file = 'negative-words.txt';
my $p_file = 'positive-words.txt';

open my $nf, $n_file or die $!;
open my $pf, $p_file or die $!;
open my $out, ">hu-liu-opinion-wordlist1.txt" or die $!;
while(<$pf>){
	my $line = $_;
	chomp $line;
	next if $line =~ /^;/;
	next unless $line =~ /\w+/;
	$line .= "\t1\n";
	print $out $line;
}
while(<$nf>){
	my $line = $_;
	chomp $line;
	next if $line =~ /^;/;
	next unless $line =~ /\w+/;
	$line .= "\t-1\n";
	print $out $line;
}
close $nf;
close $pf;
close $out;
