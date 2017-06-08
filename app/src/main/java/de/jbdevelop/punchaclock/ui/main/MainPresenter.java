package de.jbdevelop.punchaclock.ui.main;

import java.util.List;

import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.model.Area;

/**
 * Created by jan on 29.10.15.
 */
public class MainPresenter {
    MainActivity view;

    public MainPresenter(MainActivity view) {
        this.view = view;
    }

    public void updateAreas() {
        List<Area> allAreas = AreaRepository.getInstance().getAll();
        view.adapter.clear();
        view.adapter.addAll(allAreas);
        view.adapter.notifyDataSetChanged();
    }
}
