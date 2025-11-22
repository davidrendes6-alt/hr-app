# Use Node.js on Alpine
FROM node:20-alpine

# Install dependencies required for native modules
RUN apk add --no-cache python3 make g++

WORKDIR /app

# Copy package files
COPY package*.json ./

# Remove package-lock.json if it exists and do fresh install
RUN rm -f package-lock.json && npm install --include=optional

# Copy application code
COPY . .

# Expose Vite dev server port
EXPOSE 5173

# Set environment to expose on all interfaces
ENV HOST=0.0.0.0

# Run dev server
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
