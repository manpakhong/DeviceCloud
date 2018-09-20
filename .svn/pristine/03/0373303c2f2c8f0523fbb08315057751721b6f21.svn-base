#!/usr/bin/perl

$tmppath="/tmp";

$gid=0;

open (fhen, ">timezones.properties") || die;

print fhen "[timezones]\n";

$tmpfile="$tmppath/$tab.$$";

system ("curl -o $tmpfile 'https://docs.google.com/spreadsheet/pub?key=0Atruyh-2LGpjdF82SVN3SXlTZnlLc3dQUE00cmJob1E&single=true&gid=0&output=csv'");

open (fh, $tmpfile) ||die;
#-d "stringtable" || mkdir ("stringtable",0755)||die;

my $dummy=<fh>;
while (<fh>){
	chomp;
	while ( s/"([^"]+),([^"]+)"/myfunc($1,$2)/eg ) { }
	($id,$dummy,$utc,$name,$file,$dst)=split(/,/);
	if ( $id eq "" && $file ne "" ) {
		die "Error 1";
	}
	if ( $id ne "" && $file eq "" ) {
		die "Error 2";
	}
	next if ($id eq "");

	$dst eq "0" || $dst eq "1" || die;
	$utc ne "" || die;

	$utc=~s/\:$//;
	$utc=~s/GMT([\+\-]\d+)$/UTC$1\:00/;
	$utc=~s/GMT([\+\-]\d+)\.5$/UTC$1\:30/;
	$utc=~s/GMT/UTC/;
	$name=~s/MYCOMMA/,/g; $name=~s/^"//g; $name=~s/"$//g;
	$name=~tr/A-Z/a-z/;
	$name=~s/\s+/_/g;
	$name=~s/\W//g;
	
	print fhen "$id:$file\n";
}
close fh;
unlink $tmpfile;
print fhen "\n";

close fhen;

sub myfunc {
	return "\"".$_[0]."MYCOMMA".$_[1]."\"";
}

