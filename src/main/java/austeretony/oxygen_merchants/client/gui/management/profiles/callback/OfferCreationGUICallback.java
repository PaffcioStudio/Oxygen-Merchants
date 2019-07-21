package austeretony.oxygen_merchants.client.gui.management.profiles.callback;

import austeretony.alternateui.screen.browsing.GUIScroller;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.button.GUICheckBoxButton;
import austeretony.alternateui.screen.button.GUISlider;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.panel.GUIButtonPanel;
import austeretony.alternateui.screen.panel.GUIButtonPanel.GUIEnumOrientation;
import austeretony.alternateui.screen.text.GUITextField;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.itemstack.ItemStackWrapper;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen.util.OxygenUtils;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;
import austeretony.oxygen_merchants.client.gui.management.ProfilesManagementGUISection;
import austeretony.oxygen_merchants.client.gui.management.profiles.InventoryItemGUIButton;
import austeretony.oxygen_merchants.common.main.MerchantOffer;
import net.minecraft.item.ItemStack;

public class OfferCreationGUICallback extends AbstractGUICallback {

    private final ManagementMenuGUIScreen screen;

    private final ProfilesManagementGUISection section; 

    private GUIButton confirmButton, cancelButton;

    private GUIButtonPanel itemsPanel;

    private GUITextField amountField, buyCostField, sellingCostField;

    private GUICheckBoxButton enableBuyUpButton;

    private InventoryItemGUIButton currentButton;

    public OfferCreationGUICallback(ManagementMenuGUIScreen screen, ProfilesManagementGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;   
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new OfferCreationCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));//main background 1st layer

        this.addElement(new GUITextLabel(2, 2).setDisplayText(ClientReference.localize("merchants.gui.management.offerCreationCallback"), true, GUISettings.instance().getTitleScale()));

        this.addElement(new GUITextLabel(2, 98).setDisplayText(ClientReference.localize("merchants.gui.management.amount"), true, GUISettings.instance().getSubTextScale()));
        this.addElement(this.amountField = new GUITextField(3, 108, 60, 10).setScale(0.7F).enableDynamicBackground().setText("1").enableNumberFieldMode(Integer.MAX_VALUE));

        this.addElement(new GUITextLabel(2, 118).setDisplayText(ClientReference.localize("merchants.gui.management.cost"), true, GUISettings.instance().getSubTextScale()));
        this.addElement(this.buyCostField = new GUITextField(3, 128, 60, 10).setScale(0.7F).enableDynamicBackground().setText("0").enableNumberFieldMode(Integer.MAX_VALUE));

        this.addElement(this.enableBuyUpButton = new GUICheckBoxButton(2, 138, 6).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor()));
        this.addElement(new GUITextLabel(12, 137).setDisplayText(ClientReference.localize("merchants.gui.management.enableSelling"), true, GUISettings.instance().getSubTextScale()));
        this.addElement(this.sellingCostField = new GUITextField(3, 148, 60, 10).setScale(0.7F).enableDynamicBackground().setText("0").enableNumberFieldMode(Integer.MAX_VALUE).disable());

        this.itemsPanel = new GUIButtonPanel(GUIEnumOrientation.VERTICAL, 0, 12, 137, 16).setButtonsOffset(1).setTextScale(GUISettings.instance().getTextScale());
        this.addElement(this.itemsPanel);       
        GUIScroller scroller = new GUIScroller(36, 5);
        this.itemsPanel.initScroller(scroller);
        GUISlider slider = new GUISlider(this.getX() + 138, this.getY() + 12, 2, 84);
        slider.setDynamicBackgroundColor(GUISettings.instance().getEnabledSliderColor(), GUISettings.instance().getDisabledSliderColor(), GUISettings.instance().getHoveredSliderColor());
        scroller.initSlider(slider);

        this.addElement(this.confirmButton = new GUIButton(15, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.confirmButton"), true, GUISettings.instance().getButtonTextScale()).disable());
        this.addElement(this.cancelButton = new GUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.cancelButton"), true, GUISettings.instance().getButtonTextScale()));
    }

    private void loadItems() {
        this.itemsPanel.reset();

        InventoryItemGUIButton button;
        for (ItemStack itemStack : ClientReference.getClientPlayer().inventory.mainInventory) {
            if (!itemStack.isEmpty()) {
                button = new InventoryItemGUIButton(itemStack);
                button.enableDynamicBackground(GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getHoveredElementColor());
                button.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());

                this.itemsPanel.addButton(button);
            }              
        }

        this.itemsPanel.getScroller().resetPosition();
        this.itemsPanel.getScroller().getSlider().reset();
    }

    @Override
    protected void onOpen() {
        if (this.currentButton != null) {
            this.currentButton.setToggled(false);
            this.currentButton = null;
        }

        this.amountField.setText("1");
        this.buyCostField.setText("0");
        this.enableBuyUpButton.setToggled(false);
        this.sellingCostField.setText("0");
        this.sellingCostField.disable();

        this.loadItems();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) { 
            if (element == this.cancelButton)
                this.close();
            else if (element == this.confirmButton) {
                MerchantOffer offer = new MerchantOffer(OxygenUtils.createDataStampedId(), ItemStackWrapper.getFromStack(this.currentButton.getItemStack()));
                offer.setAmount(this.amountField.getTypedNumber());
                offer.setBuyCost(this.buyCostField.getTypedNumber());
                offer.setSellingEnabled(this.enableBuyUpButton.isToggled());
                offer.setSellingCost(this.sellingCostField.getTypedNumber());
                this.section.addOfferToCurrentProfile(offer);
                this.close();
            } else if (element == this.enableBuyUpButton) {
                if (this.enableBuyUpButton.isToggled())
                    this.sellingCostField.enable();
                else
                    this.sellingCostField.disable();
            } else if (element instanceof InventoryItemGUIButton) {
                InventoryItemGUIButton button = (InventoryItemGUIButton) element;
                if (this.currentButton != button) {
                    if (this.currentButton != null)
                        this.currentButton.setToggled(false);
                    button.toggle();                    
                    this.currentButton = button;

                    this.confirmButton.enable();
                }
            }
        }
    }
}
