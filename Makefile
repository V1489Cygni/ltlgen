JAVAC = javac ${JAVACFLAGS}

JAVACFLAGS = ${FLAGS} ${RESOURCES}
FLAGS =
RESOURCES = -cp resources/ecj.22.jar:resources/verifier.jar:.

DIRS = ltlgen/*.java \
	   ltlgen/fitnesses/*.java \
	   ltlgen/formulas/*.java

JAVA = java ${JAVAFLAGS}
JAVAFLAGS = ${JFLAGS} ${RESOURCES}
JFLAGS =

all:
	${JAVAC} ${DIRS}

run: all
	${JAVA} ec.Evolve -file params/ltlgen.params

clean:
	find ltlgen -name "*.class" -exec rm -f {} \;
