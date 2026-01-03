import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

// Thunk to fetch samples
export const fetchSamples = createAsyncThunk(
  'samples/fetchSamples',
  async () => {
    const response = await axios.get('/api/samples');
    return response.data;
  }
);

const samplesSlice = createSlice({
  name: 'samples',
  initialState: {
    list: [],
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchSamples.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchSamples.fulfilled, (state, action) => {
        state.loading = false;
        state.list = action.payload;
      })
      .addCase(fetchSamples.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  },
});

export default samplesSlice.reducer;
