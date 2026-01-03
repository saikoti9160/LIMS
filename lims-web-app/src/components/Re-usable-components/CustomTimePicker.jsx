import React, { useState, useEffect, useRef } from "react";
import "./CustomTimePicker.css";
import { LocalizationProvider, MobileTimePicker } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
 
const CustomTimePicker = ({ value, onChange, disabled }) => {
  const [time, setTime] = useState(null);
  const timePickerRef = useRef(null);
 
  useEffect(() => {
 
    if (value) {
      const dayjsValue = dayjs.isDayjs(value) ? value : dayjs(value);
      setTime(dayjsValue.isValid() ? dayjsValue : null);
    } else {
      setTime(null);
    }
  }, [value]);
 
  const handleTimeChange = (newValue) => {
    const validValue = newValue && dayjs.isDayjs(newValue) ? newValue : null;
    setTime(validValue);
    if (onChange && validValue) {
      onChange(validValue);
    }
  };
 
  return (
    <div className="custom-time-picker" ref={timePickerRef}>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <MobileTimePicker
          value={time}
          onChange={handleTimeChange}
          ampm
          disabled={disabled}
          views={["hours", "minutes"]}
          slotProps={{
            toolbar: {
              title: "Pick a Time",
            },
            textField: {
              variant: "outlined",
              placeholder: "HH:MM",
              className: "custom-time-picker",
              error: false,
              inputProps: {
                style: { textTransform: "uppercase" },
                readOnly: true
              }
            },
            popper: {
              className: "custom-time-picker-popup"
            },
          }}
        />
      </LocalizationProvider>
    </div>
  );
};
 
export default CustomTimePicker;