gp
==

LTL-formulas generation using genetic programming.

Place "ltlgen" folder into "ec/app" folder in ECJ.
Use "javac -cp verifier.jar ec/app/ltlgen/*.java" to compile classes.
Use "java -cp verifier.jar:. ec.Evolve -file ec/app/ltlgen/ltlgen.params" to run the program.
Detailed stats will be in "result.stat" file.
The program expects to find the automaton generator in its working directory.
