package GUI;

import java.io.File;

public class UI {
	public static SimManager restart(File testFile) {
        File file = new File("data.obj"); 
        file.delete();
        SimManager monitorSim = new SimManager();
        monitorSim.uploadStartingFile(testFile);

    	monitorSim.renderSpaceRegion();
    	monitorSim.renderDroneStatus();
		monitorSim.setDroneDirection_();

        Utils.serialize(monitorSim);
        return monitorSim;
	}
	
	public static SimManager step() {
		int curDrone;
		SimManager monitorSim =  Utils.deserialize();
		if (monitorSim.isTerminated()) return monitorSim;
    	do {
    		curDrone = monitorSim.getCurDroneId() + 1;
    	} while(!monitorSim.executeDroneAction(curDrone));


    	monitorSim.renderSpaceRegion();
    	monitorSim.renderDroneStatus();
    	monitorSim.renderSimStatus(curDrone);
		monitorSim.setDroneDirection_();

    	Utils.serialize(monitorSim);
		return monitorSim;
	}

	public static SimManager Resume() {
		SimManager monitorSim =  Utils.deserialize();
		if (monitorSim == null) return null;

		Utils.serialize(monitorSim);
		return monitorSim;
	}

	public static SimManager getSim(){
		SimManager sim = UI.step();
		return sim;
	}
}
