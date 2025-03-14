package com.shivu.swiggy_app.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

public class PaginationService 
{
 public static Map<String, Object> getPageData(Page<?> page)
  {
	  Map<String, Object> response =  new HashMap<>();
		response.put("status", "success");
		response.put("totalPages", page.getTotalPages());
		response.put("totalElements", page.getTotalElements());
		response.put("currentPage", page.getNumber());
		response.put("pageSize", page.getSize());
		response.put("hasNext", page.hasNext());
		response.put("hasPrevious", page.hasPrevious());
		return response;
  }
}
