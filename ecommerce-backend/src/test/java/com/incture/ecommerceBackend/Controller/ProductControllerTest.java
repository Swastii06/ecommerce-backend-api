package com.incture.ecommerceBackend.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incture.ecommerceBackend.Entity.Product;
import com.incture.ecommerceBackend.Service.ProductService;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProductService productService;

	// Remove @Autowired and just declare the variable
	private ObjectMapper objectMapper;

	private Product testProduct;

	@BeforeEach
	void setUp() {
		// Manually initialize ObjectMapper here
		objectMapper = new ObjectMapper();

		testProduct = new Product();
		testProduct.setId(1L);
		testProduct.setName("Controller Test Laptop");
		testProduct.setPrice(new BigDecimal("999.99"));
	}

	@DisplayName("POST /api/products - Success")
	@Test
	void testAddProduct() throws Exception {
		when(productService.addProduct(any(Product.class))).thenReturn(testProduct);

		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testProduct))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Controller Test Laptop"));
	}

	@DisplayName("GET /api/products/{id} - Success")
	@Test
	void testGetProductById() throws Exception {
		when(productService.getProductById(1L)).thenReturn(testProduct);

		mockMvc.perform(get("/api/products/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Controller Test Laptop"));
	}

	@DisplayName("GET /api/products (Paginated) - Success")
	@Test
	void testGetAllProducts() throws Exception {
		Page<Product> productPage = new PageImpl<>(Arrays.asList(testProduct));

		when(productService.getAllProducts(anyInt(), anyInt(), anyString(), any(), any())).thenReturn(productPage);

		mockMvc.perform(get("/api/products").param("page", "0").param("size", "10")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value("Controller Test Laptop"));
	}

	@DisplayName("PUT /api/products/{id} - Success")
	@Test
	void testUpdateProduct() throws Exception {
		when(productService.updateProduct(any(Long.class), any(Product.class))).thenReturn(testProduct);

		mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testProduct))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Controller Test Laptop"));
	}

	@DisplayName("DELETE /api/products/{id} - Success")
	@Test
	void testDeleteProduct() throws Exception {
		mockMvc.perform(delete("/api/products/1")).andExpect(status().isOk())
				.andExpect(content().string("Product deleted successfully"));
	}
}