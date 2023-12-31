package step;

import datadefinition.*;

import java.io.*;
import java.util.*;

public class FilesContentExtractor extends Step {
    public FilesContentExtractor(String name, boolean continue_if_failing) {
        super(name, true, continue_if_failing);
        defaultName = "Files Content Extractor";

        DataList<File> dataList = new DataList("FILES_LIST");
        inputs.add(new Input(dataList, false, true, "Files to extract"));
        nameToInputIndex.put("FILES_LIST", 0);

        DataNumber dataNumber = new DataNumber("LINE");
        inputs.add(new Input(dataNumber, true, true, "Line number to extract"));
        nameToInputIndex.put("LINE", 1);

        DataRelation dataRelation = new DataRelation("DATA");
        outputs.add(new Output(dataRelation, "Data extraction"));
        nameToOutputIndex.put("DATA", 0);

    }

    @Override
    public void run() {
        Long startTime = System.currentTimeMillis();
        List<File> files = (List<File>) inputs.get(0).getData();
        Integer line_number = (Integer) inputs.get(1).getData();
        Relation relation = new Relation(new String[]{"Index", "File name", "the info that been extracted"});
        boolean problem = false, extracted = false;
        line_number = line_number - 1;

        if (!checkGotInputs(2)) {
            runTime = System.currentTimeMillis() - startTime;
            return;
        }

        if (files.size() != 0) {
            Integer index = 1;
            for (File file : files) {
                addLineToLog("About to start work on file " + file.getName());
                Map<String, String> row = new HashMap<>();
                row.put("Index", index.toString());
                row.put("File name", file.getName());
                if (file.exists()) {
                    try (Scanner scanner = new Scanner(file)) {
                        int i;
                        for (i = 0; scanner.hasNextLine() && i < line_number; ++i)
                            scanner.nextLine();

                        if (i == line_number && scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            row.put("the info that been extracted", line);
                            extracted = true;
                        } else {
                            addLineToLog("Problem extracting line number " + (line_number + 1)
                                    + " from file " + file.getName());
                            row.put("the info that been extracted", "Not such line");
                            problem = true;
                        }

                    } catch (FileNotFoundException e) {
                        addLineToLog("Problem extracting line number " + (line_number + 1)
                                + " from file " + file.getName());
                        row.put("the info that been extracted", "File not found");
                        problem = true;
                    }
                } else {
                    addLineToLog("Problem extracting line number " + (line_number + 1)
                            + " from file " + file.getName());
                    row.put("the info that been extracted", "File not found");
                    problem = true;
                }
                relation.addRow(row);
                index++;

            }
            addLineToLog("Finished extracting the content from the given files");
            summaryLine = "Step ended successfully, ";

            if (!problem)
                summaryLine += "the content from all the given files was extracted";
            else if (extracted)
                summaryLine += "the content from part of the given files was extracted";
            else
                summaryLine += "the content was not extracted from the given files";
        } else {
            addLineToLog("No files given to extract content from");
            summaryLine = "Step ended successfully, no files given to extract content from";
        }
        outputs.get(0).setData(relation);
        stateAfterRun = State.SUCCESS;
        runTime = System.currentTimeMillis() - startTime;
    }
}
