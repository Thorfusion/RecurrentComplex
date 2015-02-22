/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.gui.table;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 02.06.14.
 */
public class TableElementPresetAction extends TableElementDefault
{
    protected GuiButton changePresetButton;
    protected GuiButton runActionButton;

    protected String currentActionID;

    protected String actionTitle;
    protected TableElementButton.Action[] actions;

    protected List<TableElementActionListener> listeners = new ArrayList<>();

    protected float actionButtonWidth = 0.4f;

    public TableElementPresetAction(String id, String title, String actionTitle, TableElementButton.Action... actions)
    {
        super(id, title);
        this.actionTitle = actionTitle;
        this.actions = actions;
        currentActionID = actions[0].id;
    }

    public void addListener(TableElementActionListener listener)
    {
        listeners.add(listener);
    }

    public void removeListener(TableElementActionListener listener)
    {
        listeners.remove(listener);
    }

    public List<TableElementActionListener> listeners()
    {
        return Collections.unmodifiableList(listeners);
    }

    public TableElementButton.Action[] getActions()
    {
        return actions;
    }

    public float getActionButtonWidth()
    {
        return actionButtonWidth;
    }

    public void setActionButtonWidth(float actionButtonWidth)
    {
        this.actionButtonWidth = actionButtonWidth;
    }

    @Override
    public void initGui(GuiTable screen)
    {
        super.initGui(screen);

        Bounds bounds = bounds();

        int curIndex = currentActionIndex();
        String title = curIndex >= 0 ? actions[curIndex].title : currentActionID;

        int presetButtonWidth = MathHelper.floor_float(bounds.getWidth() * (1f - actionButtonWidth)) - 1;
        changePresetButton = new GuiButton(-1, bounds.getMinX(), bounds.getMinY() + (bounds.getHeight() - 20) / 2, presetButtonWidth, 20, title);
        changePresetButton.visible = !isHidden();
        screen.addButton(this, 0, changePresetButton);

        runActionButton = new GuiButton(-1, bounds.getMinX() + presetButtonWidth + 2, bounds.getMinY() + (bounds.getHeight() - 20) / 2, MathHelper.floor_float(bounds.getWidth() * actionButtonWidth) - 1, 20, actionTitle);
        runActionButton.visible = !isHidden();
        screen.addButton(this, 1, runActionButton);

        setActionButtonActive();
    }

    @Override
    public void setHidden(boolean hidden)
    {
        super.setHidden(hidden);

        if (changePresetButton != null)
            changePresetButton.visible = !hidden;
        if (runActionButton != null)
            runActionButton.visible = !hidden;
    }

    @Override
    public void buttonClicked(int buttonID)
    {
        super.buttonClicked(buttonID);

        if (buttonID == 0)
        {
            TableElementButton.Action newAction = actions[(currentActionIndex() + 1) % actions.length];
            currentActionID = newAction.id;
            changePresetButton.displayString = newAction.title;

            setActionButtonActive();
        }
        else if (buttonID == 1)
        {
            for (TableElementActionListener listener : listeners)
                listener.actionPerformed(this, currentActionID);
        }
    }

    private int currentActionIndex()
    {
        int currentIndex = -1;

        for (int i = 0; i < actions.length; i++)
        {
            TableElementButton.Action action = actions[i];

            if (action.id.equals(currentActionID))
                currentIndex = i;
        }

        return currentIndex;
    }

    private void setActionButtonActive()
    {
        if (runActionButton != null)
        {
            int currentActionIndex = currentActionIndex();
            runActionButton.enabled = currentActionIndex >= 0 && actions[currentActionIndex].enabled;
        }
    }
}
