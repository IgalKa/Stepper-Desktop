package step;

import datadefinition.DataNumber;
import datadefinition.Input;


public class SpendSomeTime extends Step {
    public SpendSomeTime(String name, boolean continue_if_failing) {
        super(name, true, continue_if_failing);
        defaultName = "Spend some Time";

        DataNumber dataNumber = new DataNumber("TIME_TO_SPEND");
        inputs.add(new Input(dataNumber, true, true, "Total sleeping time(sec)"));
        nameToInputIndex.put("TIME_TO_SPEND", 0);
    }


    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        Integer sleeping_time = (Integer) inputs.get(0).getData();

        if (!checkGotInputs(1)) {
            runTime = System.currentTimeMillis() - startTime;
            return;
        }

        if (sleeping_time <= 0) {
            state_after_run = State.FAILURE;

            addLineToLog("Failed to Run the step " + getName()
                    + " because the given time is a non-positive number");

            summaryLine = "Step failed, the given time is a non-positive number";
        } else {
            try {
                addLineToLog("About to sleep for " + sleeping_time + " seconds");
                Thread.sleep(sleeping_time * 1000);
                addLineToLog("Done sleeping");
                summaryLine = "Step ended successfully, Done sleeping";
                state_after_run = State.SUCCESS;
            } catch (InterruptedException e) {
                state_after_run = State.FAILURE;
                addLineToLog("Failed to Run the step " + getName() + " because of an internal problem");

                summaryLine = "Step failed, an internal problem";
                throw new RuntimeException(e);
            }
        }
        runTime = System.currentTimeMillis() - startTime;

    }
}