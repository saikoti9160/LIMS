import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  user: null,
  isAuthenticated: false,
  role: 'guest', // "guest", "user", "admin"
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setUser(state, action) {
      state.user = action.payload;
      state.isAuthenticated = true;
      state.role = action.payload?.roleName;
    },
    logout(state) {
      state.user = null;
      state.isAuthenticated = false;
      state.role = 'guest';
      localStorage.removeItem('userDetails');
    },
  },
});

export const { setUser, logout } = userSlice.actions;
export default userSlice.reducer;
