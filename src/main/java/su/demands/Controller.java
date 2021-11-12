package su.demands;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {

    @Getter
    private int countUniqueOperators,
                countUniqueOperands,
                countOperators,
                countOperands;

    String[] operators = {
            "program", "uses", "var", "begin", "end.", "end", "writeln", "write", "readln", "read", ":=",
            "if", "else", " > ", " < ", " = ", " >= ", " <= ", " <> ", " and ", " or ", " not ",
            " xor ", "for", "while", "\\+", "-", "\\*", "/", " div ", " mod ", "integer;", "string;",
            "boolean;", "double;"
    };

    @Getter
    List<String> foundOperators = new ArrayList<>(Collections.emptyList());
    @Getter
    List<String> foundUniqueOperators = new ArrayList<>(Collections.emptyList());
    @Getter
    List<String> foundOperands= new ArrayList<>(Collections.emptyList());
    @Getter
    List<String> foundUniqueOperands = new ArrayList<>(Collections.emptyList());

    public Controller(String file) {
        List<String> text = new ArrayList<>(getDataFromResource(file));

        int varStart = 0;
        int varEnd = 0;
        String temp = "";

        for (String line : text) {
            if (line.contains("var")) varStart = text.indexOf(line);
            if (line.contains("begin")) varEnd = text.indexOf(line) - 1;
        }

        for (int i = varStart; i <= varEnd; i++) {
            temp += text.get(i) + ",";
        }

        for (String line : text) {
            if (line.contains("var")) line = temp;
            for (String operator : operators) {
                String buffer = line.replaceAll(operator, "proven");
                if (buffer.contains("proven")) {
                    foundOperators.add(operator);
                    countOperators++;

                    if (line.contains("var")) {
                        continue;
                    }
                    if (!line.contains(":=")) break;
                }
            }
        }

        foundUniqueOperators.addAll(new HashSet<>(foundOperators));
        countUniqueOperators = foundUniqueOperators.size();

        if (varEnd > 0) {
            temp = temp.replace("var","");
            temp = temp.replaceAll("(:.*?;)|\\s", "");
            List<String> afterTemp = new ArrayList<>(List.of(temp.split(",")));

            foundOperands.addAll(afterTemp);
            countOperands = afterTemp.size();

            foundUniqueOperands.addAll(new HashSet<>(afterTemp));
            countUniqueOperands = foundUniqueOperands.size();
        }
    }

    @SneakyThrows
    private List<String> getDataFromResource(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {file = getFileFromResource("test.txt");}
        return new BufferedReader(new FileReader(file)).lines()
                .map(String::toLowerCase).collect(Collectors.toList());
    }

    @SneakyThrows
    private File getFileFromResource(String fileName){
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
    }

    public double getConditionDevProgram() {
        return getProgramScope() / getProgramLevel();
    }

    public double getProgramScope() {
        return getProgramLength()*Math.log(getProgramDictionary());
    }

    public double getProgramLength() {
        return countOperators + countOperands;
    }

    public double getProgramDictionary() {
        return countUniqueOperators + countUniqueOperands;
    }

    public double getProgramLevel() {
        return 1 / getProgramDifficulties();
    }

    public double getProgramDifficulties() {
        return (countUniqueOperators / 2) * (countOperands / countUniqueOperands);
    }
}
