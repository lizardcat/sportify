package com.example.sportify;

public class Workout {
    public String date;
    public String type;
    public double duration;
    public double weight;
    public int sets;
    public int reps;
    public String notes;

    public Workout(String date, String type, double duration,
                   int sets, int reps, double weight, String notes) {
        this.date = date;
        this.type = type;
        this.duration = duration;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.notes = notes;
    }
}
