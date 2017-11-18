package com.midnightbits.avalonreveal.avalon;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

public final class Cards {
    private final List<Card> cards_;

    private Cards(List<Card> cards) {
        cards_ = cards;
    }

    public final List<Card> cards() {
        return cards_;
    }
    public final int size() { return cards_.size(); }
    public final Card get(int pos) { return cards_.get(pos); }

    public static Cards make(G.dialog[] options) {
        boolean showReminders = false;
        for (G.dialog dialog : options) {
            if (dialog == G.dialog.warnings) {
                showReminders = true;
                break;
            }
        }
        List<Card> result = new ArrayList<>();
        int card_id = 1;
        for (Line[] group : G.lines) {
            List<DialogItem> out = new ArrayList<>();
            for (Line line : group) {
                DialogItem item = line.get(options);
                if (item == null)
                    continue;
                if (!showReminders && item.text.action == G.action.warning)
                    continue;

                out.add(item);
            }
            if (out.size() > 0)
                result.add(new Card(card_id, out));
            ++card_id;
        }
        return new Cards(result);
    }

    public String expandString(Resources res) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (Card card : cards_) {
            if (first) first = false;
            else sb.append("\n    --------------------------\n");
            sb.append(card.expandString(res));
        }

        return sb.toString();
    }
}
