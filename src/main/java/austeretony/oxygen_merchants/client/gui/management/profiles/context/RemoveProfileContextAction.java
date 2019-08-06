package austeretony.oxygen_merchants.client.gui.management.profiles.context;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen_merchants.client.gui.management.ProfilesManagementGUISection;

public class RemoveProfileContextAction extends AbstractContextAction {

    private final ProfilesManagementGUISection section;

    private final String name;

    public RemoveProfileContextAction(ProfilesManagementGUISection section) {
        this.section = section;
        this.name = ClientReference.localize("oxygen_merchants.gui.management.remove");
    }

    @Override
    protected String getName(GUIBaseElement currElement) {
        return this.name;
    }

    @Override
    protected boolean isValid(GUIBaseElement currElement) {
        return true;
    }

    @Override
    protected void execute(GUIBaseElement currElement) {
        this.section.openRemoveProfileCallback();
    }
}
