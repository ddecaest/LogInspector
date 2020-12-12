package com.loginspector

import spock.lang.Specification

class TerminalArgumentParserTest extends Specification {

    def handlesNull() {
        when:
        TerminalArgumentParser.parse(null)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'Expected exactly two arguments: the path to the log file to inspect and the path to the output file!'
    }

    def handlesInvalidArgs() {
        when:
        TerminalArgumentParser.parse(illegalArgs)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'Expected exactly two arguments: the path to the log file to inspect and the path to the output file!'

        where:
        illegalArgs << [(String[]) [], (String[]) ["a"], (String[]) ["a", "b", "c"]]
    }

    def handlesValidArgs() {
        given:
        def args = (String[])['a', 'b']

        when:
        def arguments = TerminalArgumentParser.parse(args)

        then:
        arguments.pathToLogFile == 'a'
        arguments.pathToOutputFile == 'b'

    }
}
