package com.ubb.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RaceEvent extends Event{
    private double maxTime = 0;
    public RaceEvent(String id, String title) {
        super(id, title);
    }

    /**
     * Selects the apropiate ducks for each lane so that the race takes the shortest time.
     * @param ducks the list of the swimming ducks to be selected.
     */
    public String startEvent(List<SwimmingDuck> ducks, List<Lane> lanes) {

        //sortare rate descrescator dupa rezistenta, descrescator dupa viteza
        boolean swapped;
        do {
            swapped = false;
            for (int i = 0; i < ducks.size() - 1; i++) {
                boolean cond;
                    cond = ducks.get(i).getEndurance() < ducks.get(i + 1).getEndurance() ||
                            (ducks.get(i).getEndurance() == ducks.get(i + 1).getEndurance() &&
                                    ducks.get(i).getSpeed() < ducks.get(i + 1).getSpeed());
                if (cond) {
                    SwimmingDuck temp = ducks.get(i);
                    ducks.set(i, ducks.get(i + 1));
                    ducks.set(i + 1, temp);
                    swapped = true;
                }
            }
        } while (swapped);

        //sortare culoare crescator dupa distanta
        for (int i = 0; i < lanes.size() - 1; i++) {
            for (int j = i + 1; j < lanes.size(); j++) {
                if (lanes.get(i).getDistance() > lanes.get(j).getDistance())
                {
                    Lane temp = lanes.get(i);
                    lanes.set(i, lanes.get(j));
                    lanes.set(j, temp);
                }
            }
        }

        //lista in care tin minte ratele alese
        List<SwimmingDuck> chosen = new ArrayList<>(lanes.size());
        for (int i = 0; i < lanes.size(); i++) {
            chosen.add(ducks.get(i));
        }

        //sortare rate descrescator dupa rezistenta, crescator dupa viteza
        for (int i = 0; i < lanes.size() - 1; i++) {
            for (int j = i + 1; j < lanes.size(); j++) {
                if (chosen.get(i).getEndurance() < chosen.get(j).getEndurance() ||
                        (chosen.get(i).getEndurance() == chosen.get(j).getEndurance() &&
                                chosen.get(i).getSpeed() > chosen.get(j).getSpeed())) {
                    SwimmingDuck tmp = chosen.get(i);
                    chosen.set(i, chosen.get(j));
                    chosen.set(j, tmp);
                }
            }
        }

        //calcul timpi pentru fiecare rata de pe fiecare culoar si timpul maxim
        List<Double> times = new ArrayList<>(lanes.size());
        maxTime = 0;
        for (int i = 0; i < lanes.size(); i++) {
            times.add(chosen.get(i).getTimeForDistance(lanes.get(i).getDistance()));
            if (times.get(i) > maxTime) maxTime = times.get(i);
        }

        showResults(chosen, times, lanes);
        String res = String.valueOf(maxTime) + '\n';
        for (int i = 0; i < lanes.size(); i++) {
            res += "Duck " + chosen.get(i).getId() + " on lane " + lanes.get(i).getId() + ": t=" + times.get(i) + " seconds" + '\n';
        }
        return res;
    }

    /**
     * Shows the results of the race event.
     * @param chosen the list of the ducks chosen for each lane.
     * @param times the list of the times for each lane.
     * @param lanes the list of the lanes.
     */
    private void showResults(List<SwimmingDuck> chosen, List<Double> times, List<Lane> lanes) {
        System.out.printf("%.3f%n", maxTime);
        for (int i = 0; i < lanes.size(); i++) {
            System.out.printf("Duck %s on lane %d: t=%.3f secunde%n",
                    chosen.get(i).getId(), lanes.get(i).getId(), times.get(i));
        }
    }
}
