import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { fetchSamples } from '../../store/slices/samplesSlice';
import Header from './Header';

const FreeSamples = () => {
  const { list, loading, error } = useSelector((state) => state.samples);
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchSamples());
  }, [dispatch]);

  if (loading) return <p>Loading samples...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div className="App">
      <Header />
      <div className="main-content">
        <h2>Free Samples</h2>
        <ul>
          {list.map((sample) => (
            <li key={sample.id}>{sample.name}</li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default FreeSamples;
