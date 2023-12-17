package myservice.mynamespace.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

public class DemoEdmProvider extends CsdlAbstractEdmProvider {
	
	// Service Namespace
	public static final String NAMESPACE = "OData.Demo";

	// EDM Container
	public static final String CONTAINER_NAME = "Container";
	public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	// Entity Types Names
	public static final String ET_PRODUCT_NAME = "Product";
	public static final FullQualifiedName ET_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_PRODUCT_NAME);

	// Entity Set Names
	public static final String ES_PRODUCTS_NAME = "Products";

	@Override
	public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
		
		 // this method is called for one of the EntityTypes that are configured in the Schema
		  if(entityTypeName.equals(ET_PRODUCT_FQN)){

		    //create EntityType properties
		    CsdlProperty ProductId = new CsdlProperty().setName("ProductId").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		    CsdlProperty ProductName = new CsdlProperty().setName("ProductName").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		    CsdlProperty SupplierId = new CsdlProperty().setName("SupplierId").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		    CsdlProperty CategoryId = new CsdlProperty().setName("CategoryId").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		    CsdlProperty QuantityPerUnit = new CsdlProperty().setName("QuantityPerUnit").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		    CsdlProperty UnitPrice = new CsdlProperty().setName("UnitPrice").setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName());
		    CsdlProperty UnitsInStock = new CsdlProperty().setName("UnitsInStock").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		    CsdlProperty ReorderLevel = new CsdlProperty().setName("ReorderLevel").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		    CsdlProperty Discontinued = new CsdlProperty().setName("Discontinued").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		    CsdlProperty Revenue = new CsdlProperty().setName("Revenue").setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName());
		    CsdlProperty QtySold = new CsdlProperty().setName("QtySold").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		    CsdlProperty UnitsOnOrder = new CsdlProperty().setName("UnitsOnOrder").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());

		    // create CsdlPropertyRef for Key element
		    CsdlPropertyRef propertyRef = new CsdlPropertyRef();
		    propertyRef.setName("ID");

		    // configure EntityType
		    CsdlEntityType entityType = new CsdlEntityType();
		    entityType.setName(ET_PRODUCT_NAME);
		    entityType.setProperties(Arrays.asList(ProductId, ProductName, SupplierId, CategoryId, QuantityPerUnit, UnitPrice, UnitsInStock, ReorderLevel, Discontinued, Revenue, QtySold, UnitsOnOrder));
		    entityType.setKey(Collections.singletonList(propertyRef));

		    return entityType;
		  }

		  return null;
	}

	@Override
	public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
		
		if(entityContainer.equals(CONTAINER)){
		    if(entitySetName.equals(ES_PRODUCTS_NAME)){
		      CsdlEntitySet entitySet = new CsdlEntitySet();
		      entitySet.setName(ES_PRODUCTS_NAME);
		      entitySet.setType(ET_PRODUCT_FQN);

		      return entitySet;
		    }
		  }

		  return null;
	}

	@Override
	public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
		 
		// This method is invoked when displaying the Service Document at e.g. http://localhost:8080/DemoService/DemoService.svc
	    if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
	        CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
	        entityContainerInfo.setContainerName(CONTAINER);
	        return entityContainerInfo;
	    }

	    return null;
	}

	@Override
	public List<CsdlSchema> getSchemas() throws ODataException {
		
		// create Schema
		  CsdlSchema schema = new CsdlSchema();
		  schema.setNamespace(NAMESPACE);

		  // add EntityTypes
		  List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
		  entityTypes.add(getEntityType(ET_PRODUCT_FQN));
		  schema.setEntityTypes(entityTypes);

		  // add EntityContainer
		  schema.setEntityContainer(getEntityContainer());

		  // finally
		  List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
		  schemas.add(schema);

		  return schemas;
	}

	@Override
	public CsdlEntityContainer getEntityContainer() throws ODataException {
		 
		// create EntitySets
		  List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
		  entitySets.add(getEntitySet(CONTAINER, ES_PRODUCTS_NAME));

		  // create EntityContainer
		  CsdlEntityContainer entityContainer = new CsdlEntityContainer();
		  entityContainer.setName(CONTAINER_NAME);
		  entityContainer.setEntitySets(entitySets);

		  return entityContainer;
	}

}
