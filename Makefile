JAVAC = javac ${JAVACFLAGS}

JAVACFLAGS = ${FLAGS} ${RESOURCES}
FLAGS =

JAVA = java ${JAVAFLAGS}

JAVAFLAGS = ${JFLAGS} ${RESOURCES}
JFLAGS =

RESOURCES = -cp resources/ecj.22.jar:resources/verifier.jar:.

DIRS = ltlgen/*.java \
	   ltlgen/fitnesses/*.java \
	   ltlgen/formulas/*.java

all:
	${JAVAC} ${DIRS}

run: all
	${JAVA} ec.Evolve -file params/ltlgen.params

clean:
	find ltlgen -name "*.class" -exec rm -f {} \;
