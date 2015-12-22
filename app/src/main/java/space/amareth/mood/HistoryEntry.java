package space.amareth.mood;

import java.util.ArrayList;

/**
 * Created by Volodymyr on 22/12/2015.
 */
public class HistoryEntry
{
    ArrayList<String> whatWentWell;
    ArrayList<String> whatWentNotWell;
    ArrayList<String> whatIWillDo;
    int rating;
    long creationTime;

    public HistoryEntry(ArrayList<String> whatWentWell, ArrayList<String> whatWentNotWell, ArrayList<String> whatIWillDo, int rating)
    {
        this.whatWentWell=whatWentWell;
        this.whatWentNotWell=whatWentNotWell;
        this.whatIWillDo=whatIWillDo;
        this.rating = Math.max(0, Math.min(5, rating));
    }
}
