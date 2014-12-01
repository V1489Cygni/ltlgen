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

test:
	find results -name "ltlgen.params" -exec ${JAVA} ec.Evolve -file {} \;
	find results -name "out.stat" -exec rm -f {} \;

clean:
	find ltlgen -name "*.class" -exec rm -f {} \;
