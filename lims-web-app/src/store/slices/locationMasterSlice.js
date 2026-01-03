import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    continents:[],
    countries: [],
    // states: [],
    // cities: []
}

const locationSlice = createSlice({
    name: 'locationMaster',
    initialState,
    reducers: {
        setContinents(state, action) {
            state.continents = action.payload;
        },
        setCountries(state, action) {
            state.countries = action.payload;
        },
        setStates(state, action) {
            state.states = action.payload;
        },
        setCities(state, action) {
            state.cities = action.payload;
        }
    }
})

export const { setCountries, setStates, setCities , setContinents} = locationSlice.actions;
export default locationSlice.reducer;