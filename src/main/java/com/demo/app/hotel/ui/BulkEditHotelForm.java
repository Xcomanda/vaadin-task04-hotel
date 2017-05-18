package com.demo.app.hotel.ui;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.validator.routines.UrlValidator;

import com.demo.app.hotel.converters.DateToWorkedDaysConverter;
import com.demo.app.hotel.converters.IntegerRatingToStringConverter;
import com.demo.app.hotel.entities.Hotel;
import com.demo.app.hotel.services.CategoryService;
import com.demo.app.hotel.ui.abstractForms.AbstractEditHotelForm;
import com.vaadin.data.Binder;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

@SuppressWarnings("serial")
public class BulkEditHotelForm extends AbstractEditHotelForm {

	private NativeSelect<Component> nativeSelect;
	private Set<Hotel> hotels;
	private List<Component> fields;
	private Component currentComponent;
	private Hotel tempHotel;
	private Binder<Hotel> tempBinder;

	public BulkEditHotelForm(HotelForm hotelForm) {
		super(hotelForm);
		setWidth("700px");
		nativeSelect = new NativeSelect<Component>();
		// tempBinder.setBean(tempHotel);

		Map<AbstractComponent, String> Mapfields = new HashMap<>();
		Mapfields.put(name, "Name");
		Mapfields.put(address, "Address");
		Mapfields.put(rating, "Rating");
		Mapfields.put(operatesFrom, "Operates From");
		Mapfields.put(category, "Category");
		Mapfields.put(url, "Url");
		Mapfields.put(description, "Description");

		fields = new ArrayList<>();
		fields.add(name);
		fields.add(address);
		fields.add(rating);
		fields.add(operatesFrom);
		fields.add(category);
		fields.add(url);
		fields.add(description);

		nativeSelect.addSelectionListener(e -> {
			if (currentComponent != null) {
				currentComponent.setVisible(false);
			}
			try {
				currentComponent = nativeSelect.getSelectedItem().get();
				currentComponent.setVisible(true);
			} catch (Exception ex) {
				currentComponent = null;
			}
		});

		nativeSelect.setItemCaptionGenerator(field -> Mapfields.get(field));
		nativeSelect.setItems(fields);
		addComponentAsFirst(nativeSelect);
		addComponentAsFirst(new Label("Select field to bulk edit"));
	}

	public void setHotel(Set<Hotel> hotels) {
		tempHotel = new Hotel();
		//binder.setBean(tempHotel);
		tempBinder = new Binder<>(Hotel.class);
		tempBinder.setBean(tempHotel);
		this.hotels = hotels;
		currentComponent = null;
		nativeSelect.setSelectedItem(null);
		hideAll();
	}

	private void hideAll() {
		fields.forEach(field -> field.setVisible(false));
	}

	@Override
	protected void save() {
		tempBinder.validate();
		if (currentComponent != null) {
			if (currentComponent == name)
				tempBinder.forField(name).asRequired("Name is required").bind(Hotel::getName, Hotel::setName);
			if (currentComponent == address)
				tempBinder.forField(address).asRequired("Address is required").bind(Hotel::getAddress,
						Hotel::setAddress);
			if (currentComponent == rating)
				tempBinder.forField(rating).withConverter(new IntegerRatingToStringConverter())
						.withValidator(rating -> rating < 6 && rating > 0,
								"Rating is required and should be a number from 1 to 5")
						.bind(Hotel::getRating, Hotel::setRating);
			if (currentComponent == operatesFrom)
				tempBinder.forField(operatesFrom).withConverter(new DateToWorkedDaysConverter())
						.withValidator(date -> Duration
								.between(LocalDate.now().atTime(0, 0), operatesFrom.getValue().atTime(0, 0))
								.toDays() <= 0, "Cannot set future date")
						.bind(Hotel::getOperatesDays, Hotel::setOperatesDays);
			if (currentComponent == category)
				tempBinder.forField(category)
						.withValidator(category -> CategoryService.getInstance().findById(category) != null,
								"Select category")
						.bind(Hotel::getCategoryId, Hotel::setCategoryId);
			if (currentComponent == url)
				tempBinder.forField(url).asRequired("URL is required")
						.withValidator(url -> new UrlValidator().isValid(url),
								"URL is invalid. Try to write entire URL")
						.bind(Hotel::getUrl, Hotel::setUrl);
			if (tempBinder.isValid()) {
				if (currentComponent == name)
					hotels.forEach(hotel -> hotel.setName(tempHotel.getName()));
				if (currentComponent == address)
					hotels.forEach(hotel -> hotel.setAddress(tempHotel.getAddress()));
				if (currentComponent == rating)
					hotels.forEach(hotel -> hotel.setRating(tempHotel.getRating()));
				if (currentComponent == operatesFrom)
					hotels.forEach(hotel -> hotel.setOperatesDays(tempHotel.getOperatesDays()));
				if (currentComponent == category)
					hotels.forEach(hotel -> hotel.setCategoryId(tempHotel.getCategoryId()));
				if (currentComponent == url)
					hotels.forEach(hotel -> hotel.setUrl(tempHotel.getUrl()));
				hotels.forEach(hotel -> hotelService.save(hotel));
				hotelForm.closePopup();
				hotelForm.updateList();
			}
		}
	}

}
