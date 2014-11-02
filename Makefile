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

test: all
	${JAVA} ec.Evolve -file results/spea2/no-random/no-mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/no-random/no-mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/no-random/mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/no-random/mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/random/no-mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/random/no-mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/random/mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/random/mutants/scenarios-mutants/not-temporal-only/ltlgen.params
	${JAVA} ec.Evolve -file results/spea2/random/mutants/scenarios-mutants/temporal-only/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/no-random/no-mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/no-random/no-mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/no-random/mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/no-random/mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/random/no-mutants/no-scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/random/no-mutants/scenarios-mutants/ltlgen.params
	${JAVA} ec.Evolve -file results/nsga2/random/mutants/no-scenarios-mutants/ltlgen.params

clean:
	find ltlgen -name "*.class" -exec rm -f {} \;
