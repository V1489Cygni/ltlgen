JAVAC = javac ${JAVACFLAGS}

JAVACFLAGS = ${FLAGS} ${RESOURCES}
FLAGS =

JAVA = java ${JAVAFLAGS}

JAVAFLAGS = ${JFLAGS} ${RESOURCES}
JFLAGS =

RESOURCES = -cp resources/ecj.22.jar:resources/verifier.jar:.

DIRS = ltlgen/*.java \
	   ltlgen/fitnesses/*.java \
	   ltlgen/filters/*.java \
	   ltlgen/formulas/*.java

all:
	${JAVAC} ${DIRS}

run: all
	${JAVA} ec.Evolve -file params/ltlgen.params

test_spea2: all
	${JAVA} ec.Evolve -file results/spea2/no-random/no-mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/no-random/no-mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/no-random/mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/no-random/mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/random/no-mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/random/no-mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/random/mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/random/mutants/scenarios-mutants/ltlgen.params
	find results/spea2/ -name "out.stat" -exec rm -f {} \;

test_nsga2: all
	${JAVA} ec.Evolve -file results/nsga2/no-random/no-mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/no-random/no-mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/no-random/mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/no-random/mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/random/no-mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/random/no-mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/random/mutants/no-scenarios-mutants/ltlgen.params
	find results/nsga2/ -name "out.stat" -exec rm -f {} \;

test: test_spea2 test_nsga2

clean:
	find ltlgen -name "*.class" -exec rm -f {} \;
