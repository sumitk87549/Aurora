-- Fix the image_data column type in candle_images table
-- This migration fixes the issue where image_data was being stored as bigint instead of bytea

-- Drop the existing column if it exists with wrong type
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'candle_images' 
               AND column_name = 'image_data' 
               AND data_type = 'bigint') THEN
        ALTER TABLE candle_images DROP COLUMN image_data;
    END IF;
END
$$;

-- Add the image_data column with correct bytea type if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                  WHERE table_name = 'candle_images' 
                  AND column_name = 'image_data') THEN
        ALTER TABLE candle_images ADD COLUMN image_data bytea;
    END IF;
END
$$;
