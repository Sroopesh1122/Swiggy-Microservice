package com.shivu.swiggy_app.response;



import com.shivu.swiggy_app.entity.MenuItem;

import lombok.Data;

@Data
public class MenuItemWithCartStatus extends MenuItem {
 
	private boolean saved;
}
