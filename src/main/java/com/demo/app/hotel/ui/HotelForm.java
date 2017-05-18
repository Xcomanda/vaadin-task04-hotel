package com.demo.app.hotel.ui;

import java.util.List;
import java.util.Set;

import com.demo.app.hotel.entities.Hotel;
import com.demo.app.hotel.entities.HotelCategory;
import com.demo.app.hotel.services.CategoryService;
import com.demo.app.hotel.services.HotelService;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class HotelForm extends FormLayout implements View {
	private HotelService hotelService = HotelService.getInstance();

	private TextField filterTextByName;
	private TextField filterTextByAddress;
	private Button clearFilterByNameBtn;
	private Button clearFilterByAdressBtn;
	private Grid<Hotel> gridHotel;
	private Button addHotelBtn;
	private Button editHotelBtn;
	private Button deleteHotelBtn;
	private Button bulkUpdateHotelBtn;

	private HorizontalLayout filters;
	private HorizontalLayout toolbar;
	private CssLayout filterByName;
	private CssLayout filterByAddress;

	private PopupView popupEditHotel;
	private PopupView popupBulkEditHotel;
	private EditHotelForm editHotelForm;
	private BulkEditHotelForm bulkEditHotelForm;

	public HotelForm() {
		initComponents();
		initLayouts();

		filterByName.addComponents(filterTextByName, clearFilterByNameBtn);
		filterByAddress.addComponents(filterTextByAddress, clearFilterByAdressBtn);
		filters.addComponents(filterByName, filterByAddress);

		toolbar.addComponents(addHotelBtn, editHotelBtn, bulkUpdateHotelBtn, deleteHotelBtn);
		addComponents(new VerticalLayout(filters, toolbar, popupEditHotel, popupBulkEditHotel, gridHotel));

		updateList();
		selectionCheck();
	}

	private void initLayouts() {
		filterByName = new CssLayout();
		filterByName.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		filterByAddress = new CssLayout();
		filterByAddress.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		toolbar = new HorizontalLayout();
		filters = new HorizontalLayout();
	}

	private void initComponents() {
		filterTextByName = new TextField();
		filterTextByName.setPlaceholder("Filter by name");
		filterTextByName.addValueChangeListener(e -> updateList());
		filterTextByName.setValueChangeMode(ValueChangeMode.LAZY);

		filterTextByAddress = new TextField();
		filterTextByAddress.setPlaceholder("Filter by address");
		filterTextByAddress.addValueChangeListener(e -> updateList());
		filterTextByAddress.setValueChangeMode(ValueChangeMode.LAZY);

		clearFilterByNameBtn = new Button();
		clearFilterByNameBtn.setDescription("Clear filter by name");
		clearFilterByNameBtn.setIcon(VaadinIcons.CLOSE);
		clearFilterByNameBtn.addClickListener(e -> filterTextByName.clear());

		clearFilterByAdressBtn = new Button();
		clearFilterByAdressBtn.setDescription("Clear filter by address");
		clearFilterByAdressBtn.setIcon(VaadinIcons.CLOSE);
		clearFilterByAdressBtn.addClickListener(e -> filterTextByAddress.clear());

		gridHotel = new Grid<>(Hotel.class);
		gridHotel.setColumns("name", "address", "rating", "operatesDays");
		gridHotel.addColumn(hotel -> {
			HotelCategory category = CategoryService.getInstance().findById(hotel.getCategoryId());
			if (category != null) {
				return category.getName();
			}
			return "[not found]";
		}).setCaption("Category");
		gridHotel.addColumn(hotel -> "<a href='" + hotel.getUrl() + "' target='_blank'>link</a>", new HtmlRenderer())
				.setCaption("url");
		gridHotel.setSizeFull();
		gridHotel.setSelectionMode(SelectionMode.MULTI);
		gridHotel.addSelectionListener(e -> {
			selectionCheck();
		});

		editHotelForm = new EditHotelForm(this);
		bulkEditHotelForm = new BulkEditHotelForm(this);

		popupEditHotel = new PopupView(null, editHotelForm);
		popupEditHotel.setHideOnMouseOut(false);
		popupEditHotel.setSizeFull();
		popupBulkEditHotel = new PopupView(null, bulkEditHotelForm);
		popupBulkEditHotel.setHideOnMouseOut(false);
		popupBulkEditHotel.setSizeFull();

		addHotelBtn = new Button("New");
		addHotelBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		addHotelBtn.setClickShortcut(ShortcutAction.KeyCode.INSERT);
		addHotelBtn.addClickListener(e -> {
			editHotelForm.setHotel(new Hotel());
			popupEditHotel.setPopupVisible(true);
		});

		editHotelBtn = new Button("Edit");
		editHotelBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
		editHotelBtn.setClickShortcut(ShortcutAction.KeyCode.F2);
		editHotelBtn.addClickListener(e -> gridHotel.getSelectedItems().forEach(hotel -> {
			editHotelForm.setHotel(hotel);
			popupEditHotel.setPopupVisible(true);
		}));

		deleteHotelBtn = new Button("Delete");
		deleteHotelBtn.setStyleName(ValoTheme.BUTTON_DANGER);
		deleteHotelBtn.addClickListener(e -> gridHotel.getSelectedItems().forEach(hotel -> {
			hotelService.delete(hotel);
			updateList();
		}));

		bulkUpdateHotelBtn = new Button("Bulk update");
		bulkUpdateHotelBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
		bulkUpdateHotelBtn.addClickListener(e -> {
			Set<Hotel> hotels = gridHotel.getSelectedItems();
			bulkEditHotelForm.setHotel(hotels);
			popupBulkEditHotel.setPopupVisible(true);
		});

	}

	public void updateList() {
		List<Hotel> hotels = hotelService.findAllbyNameAndAddress(filterTextByName.getValue(),
				filterTextByAddress.getValue());
		gridHotel.setItems(hotels);
	}

	private void selectionCheck() {
		int selectedCount = gridHotel.getSelectedItems().size();
		editHotelBtn.setEnabled(selectedCount == 1);
		deleteHotelBtn.setEnabled(selectedCount > 0);
		bulkUpdateHotelBtn.setEnabled(selectedCount > 0);
	}

	public void closePopup() {
		popupEditHotel.setPopupVisible(false);
		popupBulkEditHotel.setPopupVisible(false);
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}
}
