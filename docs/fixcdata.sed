# $Id: fixcdata.sed,v 1.1 2004/07/22 00:30:34 spal Exp $
# $Source: /cvsroot/sqlunit/sqlunit/docs/fixcdata.sed,v $
# Used for postprocessing CDATA sections where newline and tabs are significant 
# which XDoclet cannot handle. We make use of meta tags inside the XDoclet
# documentation: \n is represented as {\n} and \t is represented as {\t}.
# Called as: sed -f fixcdata.sed file1 > file2
#
s/{\\n}/\
/g
s/{\\t}/  /g
