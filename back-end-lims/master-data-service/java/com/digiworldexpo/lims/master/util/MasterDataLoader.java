package com.digiworldexpo.lims.master.util;

import java.util.ArrayList;

import java.util.List;
import java.util.function.Function;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.digiworldexpo.lims.entities.master.Cities;
import com.digiworldexpo.lims.entities.master.Continents;
import com.digiworldexpo.lims.entities.master.Countries;
import com.digiworldexpo.lims.entities.master.Designation;
import com.digiworldexpo.lims.entities.master.Module;
import com.digiworldexpo.lims.entities.master.PaymentMode;
import com.digiworldexpo.lims.entities.master.Relation;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.entities.master.States;
import com.digiworldexpo.lims.master.repository.CitiesRepository;
import com.digiworldexpo.lims.master.repository.ContinentsRepository;
import com.digiworldexpo.lims.master.repository.CountriesRepository;
import com.digiworldexpo.lims.master.repository.DesignationRepository;
//import com.digiworldexpo.lims.master.repository.ModuleRepository;
import com.digiworldexpo.lims.master.repository.PaymentModeRepository;
import com.digiworldexpo.lims.master.repository.RelationRepository;
import com.digiworldexpo.lims.master.repository.RoleRepository;
import com.digiworldexpo.lims.master.repository.StatesRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MasterDataLoader implements ApplicationRunner {
    
    // Declaring all the required repositories
	private final ContinentsRepository continentsRepository;
    private final CountriesRepository countriesRepository;
    private final StatesRepository statesRepository;
    private final CitiesRepository citiesRepository;
    private final PaymentModeRepository paymentModeRepository;
    private final RoleRepository roleRepository;
    private final RelationRepository relationRepository;
    private final DesignationRepository designationRepository;
//    private final ModuleRepository moduleRepository;

    public MasterDataLoader(ContinentsRepository continentsRepository, 
    		CountriesRepository countriesRepository, 
    		StatesRepository statesRepository, 
    		CitiesRepository citiesRepository,
    		PaymentModeRepository paymentModeRepository,
    		RoleRepository roleRepository,
    		RelationRepository relationRepository,
    		DesignationRepository designationRepository
//		,ModuleRepository moduleRepository
		) {
    	this.continentsRepository = continentsRepository;
        this.countriesRepository = countriesRepository;
        this.statesRepository = statesRepository;
        this.citiesRepository = citiesRepository;
        this.paymentModeRepository = paymentModeRepository;
        this.roleRepository = roleRepository;
        this.relationRepository = relationRepository;
        this.designationRepository = designationRepository;
//        this.moduleRepository = moduleRepository;
    }
    
    // Declaring the lists for each repository to store the data into the lists from the database    
    private final List<Continents> continents = new ArrayList<>();
    private final List<Countries> countries = new ArrayList<>();
    private final List<States> states = new ArrayList<>();
    private final List<Cities> cities = new ArrayList<>();
    private final List<PaymentMode> paymentModes = new ArrayList<>();
    private final List<Role> roles = new ArrayList<>();
    private final List<Relation> relations = new ArrayList<Relation>();
    private final List<Designation> designations = new ArrayList<Designation>();
    private final List<Module> modules = new ArrayList<Module>();
 
    
    @Override
    public void run(ApplicationArguments args) {
        log.info("--------------------------------- MASTER DATA LOADING -------------------------------------");
        loadData();
    }
    
    private void loadData() {
        log.info("Fetching master data from repositories...");
        
        continents.addAll(continentsRepository.findAll());
        countries.addAll(countriesRepository.findAll());
        states.addAll(statesRepository.findAll());
        cities.addAll(citiesRepository.findAll());
        paymentModes.addAll(paymentModeRepository.findAll());
        roles.addAll(roleRepository.findAll());
        relations.addAll(relationRepository.findAll());
        designations.addAll(designationRepository.findAll());
//        modules.addAll(moduleRepository.findAll());
    }
    
    private<T> void uploadListsOfItem(List<T> existedItems, List<T> allNewItems, String itemType) {
    	existedItems.clear();
    	log.info("Before uploading {}, size: {}", itemType, existedItems.size());
    	existedItems.addAll(allNewItems);
    	log.info("Uploaded {} to cache.", itemType);
    	log.info("After uploading {}, size: {}", itemType, existedItems.size());
    }
    
    private <T> void addItemToList(List<T> list, T item, String itemType) {
		int initialSize = list.size();
		log.info("Before adding {}, size: {}", itemType, initialSize);
		list.add(item);
		log.info("Added {} to cache.", itemType);
		int finalSize = list.size();
		log.info("After adding {}, size: {}", itemType, finalSize);
	}

    private <T, R> void removeItemFromList(List<T> list, T item, String itemType, Function<T, R> getIdFunction) {
		int initialSize = list.size();
		log.info("Before removing {}, size: {}", itemType, initialSize);
		for(int i=0; i<list.size(); i ++) {
			T data = list.get(i);
			if(getIdFunction.apply(data).equals(getIdFunction.apply(item))) {
				list.remove(i);
				break;
			}
		}
		if (list.size()< initialSize) {
			log.info("Removed {} from cache.", itemType);
		} else {
			log.warn("{} not found in the cache, nothing removed.", itemType);
		}
		int finalSize = list.size();
		log.info("After removing {}, size: {}", itemType, finalSize);
	}
    
	
	public void uploadContinent(List<Continents> allNewContinents) {
		uploadListsOfItem(continents,allNewContinents, CommonConstants.CONTINENT);
	}
	
	public void uploadCountry(List<Countries> allNewCountries) {
		uploadListsOfItem(countries,allNewCountries, CommonConstants.CITY);
	}
	
	public void uploadState(List<States> allNewStates) {
		uploadListsOfItem(states,allNewStates, CommonConstants.STATE);
	}
	
	public void uploadCity(List<Cities> allNewCities) {
		uploadListsOfItem(cities,allNewCities, CommonConstants.CITY);
	}
	
	// ----> CONTINENT
	
	public void addContinent(Continents continent) {
		addItemToList(continents, continent, CommonConstants.CONTINENT);
	}

	public void updateContinent(Continents continent) {
		boolean updated = false;
		for (int i = 0; i < continents.size(); i++) {
			Continents existingContinent = continents.get(i);
			if (existingContinent.getId().equals(continent.getId())) {
				continents.set(i, continent);
				log.info("Updated continent in the cache.");
				updated = true;
				break;
			}
		}
		if (!updated) {
			log.warn("Continent not found in the cache, adding new.");
			addItemToList(continents, continent, CommonConstants.CONTINENT);
		}
	}

	public void deleteContinent(Continents continent) {
		removeItemFromList(continents, continent, CommonConstants.CONTINENT, Continents::getId);
	}
	
	// ----> COUNTRY
	
	public void addCountry(Countries country) {
		addItemToList(countries, country, CommonConstants.COUNTRY);
	}

	public void updateCountry(Countries country) {
		boolean updated = false;
		for (int i = 0; i < countries.size(); i++) {
			Countries existingCountry = countries.get(i);
			if (existingCountry.getId().equals(country.getId())) {
				countries.set(i, country);
				log.info("Updated country in the cache.");
				updated = true;
				break;
			}
		}
		if (!updated) {
			log.warn("Country not found in the cache, adding new.");
			addItemToList(countries, country, CommonConstants.COUNTRY);
		}
	}

	public void deleteCountry(Countries country) {
		removeItemFromList(countries, country, CommonConstants.COUNTRY, Countries::getId);
	}

	// ----> STATE
	
	public void addState(States state) {
		addItemToList(states, state, CommonConstants.STATE);
	}

	public void updateState(States state) {
		boolean updated = false;
		for (int i = 0; i < states.size(); i++) {
			States existingState = states.get(i);
			if (existingState.getId().equals(state.getId())) {
				states.set(i, state);
				log.info("Updated state in the cache.");
				updated = true;
				break;
			}
		}
		if (!updated) {
			log.warn("State not found in the cache, adding new.");
			addItemToList(states, state, CommonConstants.STATE);
		}
	}

	public void deleteState(States state) {
		removeItemFromList(states, state, CommonConstants.STATE, States::getId);
	}
	
	// ----> CITY
	
	public void addCity(Cities city) {
		addItemToList(cities, city, CommonConstants.CITY);
	}

	public void updateCity(Cities city) {
		boolean updated = false;
		for (int i = 0; i < cities.size(); i++) {
			Cities existingCity = cities.get(i);
			if (existingCity.getId().equals(city.getId())) {
				cities.set(i, city);
				log.info("Updated city in the cache.");
				updated = true;
				break;
			}
		}
		if (!updated) {
			log.warn("City not found in the cache, adding new.");
			addItemToList(cities, city, CommonConstants.CITY);
		}
	}

	public void deleteCity(Cities city) {
		removeItemFromList(cities, city, CommonConstants.CITY, Cities::getId);
	}
	
	// ----> PAYMENT MODE
	
	public void addPaymentMode(PaymentMode paymentMode) {
		addItemToList(paymentModes, paymentMode, CommonConstants.PAYMENT_MODE);
	}
	
	public void updatePaymentMode(PaymentMode paymentMode) {
		boolean updated = false;
		for (int i = 0; i < paymentModes.size(); i++) {
			PaymentMode existingPaymentMode = paymentModes.get(i);
			if (existingPaymentMode.getId().equals(paymentMode.getId())) {
				paymentModes.set(i, paymentMode);
				log.info("Updated payment mode in the cache.");
				updated = true;
				break;
			}
		}
		if (!updated) {
			log.warn("Payment mode not found in the cache, adding new.");
			addItemToList(paymentModes, paymentMode, CommonConstants.PAYMENT_MODE);
		}
	}

	public void deletePaymentMode(PaymentMode paymentMode) {
		removeItemFromList(paymentModes, paymentMode, CommonConstants.PAYMENT_MODE, PaymentMode::getId);
	}
	
	// ----> ROLE
	
	public void addRole(Role role) {
		addItemToList(roles, role, CommonConstants.ROLE);
	}
	
	public void updateRole(Role role) {
		boolean updated = false;
		for (int i = 0; i < roles.size(); i++) {
			Role existingRole = roles.get(i);
			if (existingRole.getId().equals(role.getId())) {
				roles.set(i, role);
				log.info("Updated role in the cache.");
				updated = true;
				break;
			}
		}
		if (!updated) {
			log.warn("Role not found in the cache, adding new.");
			addItemToList(roles, role, CommonConstants.ROLE);
		}
	}

	public void deleteRole(Role role) {
		log.info("role {}",role);
		removeItemFromList(roles, role, CommonConstants.ROLE, Role::getId);
	}
	
	// ----> RELATION
	
	public void addRelation(Relation relation) {
		addItemToList(relations, relation, CommonConstants.RELATION);
	}
	
	public void updateRelation(Relation relation) {
		boolean updated = false;
		for (int i = 0; i < relations.size(); i++) {
			Relation existingRelation = relations.get(i);
			if (existingRelation.getId().equals(relation.getId())) {
				relations.set(i, relation);
				log.info("Updated relation in the cache.");
				updated = true;
				break;
			}
		}
		if (!updated) {
			log.warn("Relation not found in the cache, adding new.");
			addItemToList(relations, relation, CommonConstants.RELATION);
		}
	}

	public void deleteRelation(Relation relation) {
		removeItemFromList(relations, relation, CommonConstants.RELATION, Relation::getId);
	}
	
	// ----> DESIGNATION
	
	public void addDesignation(Designation designation) {
		addItemToList(designations, designation, CommonConstants.DESIGNATION);
	}
	
	public void updateDesignation(Designation designation) {
		boolean updated = false;
		for (int i = 0; i < designations.size(); i++) {
			Designation existingDesignation = designations.get(i);
			if (existingDesignation.getId().equals(designation.getId())) {
				designations.set(i, designation);
				log.info("Updated designation in the cache.");
				updated = true;
				break;
			}
		}
		if (!updated) {
			log.warn("Designation not found in the cache, adding new.");
			addItemToList(designations, designation, CommonConstants.DESIGNATION);
		}
	}

	public void deleteDesignation(Designation designation) {
		removeItemFromList(designations, designation, CommonConstants.DESIGNATION, Designation::getId);
	}
	
	// ----> MODULE
	
	public void addModule(Module module) {
		addItemToList(modules, module, CommonConstants.MODULE);
	}
	
	
	
	// Providing the getters to fetch the data for the required master
    public List<Continents> getContinents() {
    	return continents;
    }
    
    public List<Countries> getCountries() {
    	return countries;
    }
    
    public List<States> getStates() {
    	return states;
    }
    
    public List<Cities> getCities() {
    	return cities;
    }
    
    public List<PaymentMode> getPaymentModes() {
    	return paymentModes;
    }
    
    public List<Role> getRoles() {
    	return roles;
    }
    
    public List<Relation> getRelations() {
    	return relations;
    }
    
    public List<Designation> getDesignations() {
    	return designations;
    }
    
    public List<Module> getModules() {
    	return modules;
    }


}
