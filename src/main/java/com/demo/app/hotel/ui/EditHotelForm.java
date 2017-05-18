package com.demo.app.hotel.ui;

import com.demo.app.hotel.entities.Hotel;
import com.demo.app.hotel.services.CategoryService;
import com.demo.app.hotel.ui.abstractForms.AbstractEditHotelForm;

@SuppressWarnings("serial")
public class EditHotelForm extends AbstractEditHotelForm {

	public EditHotelForm(HotelForm hotelForm) {
		super(hotelForm);
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
		binder.setBean(hotel);
		if (CategoryService.getInstance().findById(hotel.getCategoryId()) == null) {
			category.setSelectedItem(null);
		}
		name.selectAll();		
	}

	@Override
	protected void save() {
		if (binder.isValid()) {
		hotelService.save(hotel);
		hotelForm.updateList();
		hotelForm.closePopup();
	} else {
		binder.validate();		
	}
}
}