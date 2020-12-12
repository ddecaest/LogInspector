package com.loginspector

import spock.lang.Specification

class ArgumentParserTest extends Specification {

    def handlesNull() {
        when:
        ArgumentParser.parse(null)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'Expected exactly two arguments: the path to the log file to inspect and the path to the output file!'
    }

    def handlesInvalidNumberOfArguments() {
        when:
        ArgumentParser.parse(illegalArgs)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'Expected exactly two arguments: the path to the log file to inspect and the path to the output file!'

        where:
        illegalArgs << [(String[]) [], (String[]) ["a"], (String[]) ["a", "b", "c"]]
    }

    def handlesIncorrectArguments() {
        when:
        ArgumentParser.parse((String[]) [arg1, arg2])

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'Expected exactly two arguments: the path to the log file to inspect and the path to the output file!'

        where:
        [arg1, arg2] << [["", " ", null], ["", " ", null]].combinations()
    }

    def handlesValidArgs() {
        given:
        def args = (String[])['a', 'b']

        when:
        def arguments = ArgumentParser.parse(args)

        then:
        arguments.pathToLogFile == 'a'
        arguments.pathToOutputFile == 'b'

    }
}
