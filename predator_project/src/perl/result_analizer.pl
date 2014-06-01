#!/usr/bin/perl

use strict;
use warnings;
use Text::Table;

open my $results, '/home/hector/Dropbox/ITU/DataMining/result.csv' or die $!;
my %map;
my $num = 0;
my $inst = 0;
my $first;
while(my $line  = <$results>){
	next unless $line =~ /^\d.*/;
	my @attrs = split(";", $line);
	#print "id = $attrs[14]\nlabel = $attrs[15]\nprediction = $attrs[18]\n";
	${$map{$attrs[14]}}{id} = $attrs[15];  
	push (@{$map{$attrs[14]}{labels}}, $attrs[18]);
	$num++;
}
close $results;
my $tp = 0; 
my $tn = 0;
my $fp = 0;
my $fn = 0;
for my $key (keys %map){
	$inst++;
	my $p = ${$map{$key}}{id};
	my @labels = @{$map{$key}{labels}};
	my $l_p = 0; 
	my $l_np = 0;
	foreach my $l(@labels){
		chomp($l);
		$l_p++ if($l eq "p");
		$l_np++ if $l eq "np";
	}
	if($p eq "p"){
		if($l_p >= $l_np){
			$tp++;
		}else{
			$fn++;
		}
	}elsif($p eq "np"){
		if($l_p <= $l_np){
			$tn++;
		}else{
			$fp++;
		}

	}
}
my $tb = Text::Table->new( "actual", "predicted p:", "predicted np:");
$tb->load( [ "p:", $tp, $fn ],
       	[ "np:", $fp, $tn ]);

print $tb;

print "\nTotal tuples = $num\nTotal instances = $inst\n";
my $prec = sprintf("%.3f", ($tp/($tp+$fp)));
my $rec = sprintf("%.3f", ($tp/($tp+$fn)));
my $f_5 = sprintf("%.3f", f_mes(0.5));
my $f1 = sprintf("%.3f", f_mes(1));
my $f3 = sprintf("%.3f", f_mes(3));

my $tb2 = Text::Table->new( "precision", "recall", "f0.5", "f1", "f3");
$tb2->load( [ $prec, $rec, $f_5, $f1, $f3 ]);
print "\n";
print $tb2;


sub f_mes{
	my $fB = shift;
	return ((($fB^2)+1)*($prec*$rec))/(($fB^2)*$prec+$rec);
}
