import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  isPremium: false,
  expiryDate: null,
};

const subscriptionSlice = createSlice({
  name: 'subscription',
  initialState,
  reducers: {
    setSubscription(state, action) {
      state.isPremium = action.payload.isPremium;
      state.expiryDate = action.payload.expiryDate;
    },
  },
});

export const { setSubscription } = subscriptionSlice.actions;
export default subscriptionSlice.reducer;
