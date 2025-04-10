package com.example.sportify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private List<Workout> workoutList;

    public WorkoutAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorkoutDate, tvWorkoutSummary, tvWorkoutType;

        public ViewHolder(View view) {
            super(view);
            tvWorkoutDate = view.findViewById(R.id.tvWorkoutDate);
            tvWorkoutSummary = view.findViewById(R.id.tvWorkoutSummary);
            tvWorkoutType = view.findViewById(R.id.tvWorkoutType);
        }
    }

    @NonNull
    @Override
    public WorkoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workout workout = workoutList.get(position);

        holder.tvWorkoutDate.setText(workout.date);
        holder.tvWorkoutType.setText(workout.type);

        // Set background tag color
        switch (workout.type.toLowerCase()) {
            case "running":
                holder.tvWorkoutType.setBackgroundResource(R.drawable.activity_tag_background_red);
                break;
            case "cycling":
                holder.tvWorkoutType.setBackgroundResource(R.drawable.activity_tag_background_blue);
                break;
            case "walking":
                holder.tvWorkoutType.setBackgroundResource(R.drawable.activity_tag_background_green);
                break;
            default:
                holder.tvWorkoutType.setBackgroundResource(R.drawable.activity_tag_background_gray);
                break;
        }

        // Summary logic
        String summary;
        if (workout.sets > 0 && workout.reps > 0) {
            summary = workout.sets + " sets x " + workout.reps + " reps @ " + workout.weight + "kg";
        } else {
            summary = (int) workout.duration + " min •";
        }

        // Optional notes
        if (workout.notes != null && !workout.notes.isEmpty()) {
            summary += " • " + workout.notes;
        }

        holder.tvWorkoutSummary.setText(summary);
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }
}

