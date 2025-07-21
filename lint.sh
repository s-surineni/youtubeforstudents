#!/bin/bash

# Kotlin linting and formatting script for Android project
# Usage: ./lint.sh [check|format|apply]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# Function to run ktlint command
run_ktlint() {
    local command="$1"
    echo -e "${GREEN}Running: $command${NC}"
    
    if eval "$command"; then
        echo -e "${GREEN}✅ ktlint $command completed successfully${NC}"
        return 0
    else
        echo -e "${RED}❌ ktlint $command failed with exit code $?${NC}"
        return 1
    fi
}

# Check if action is provided
if [ $# -eq 0 ]; then
    echo -e "${RED}Error: Action parameter is required${NC}"
    echo -e "${CYAN}Usage: $0 [check|format|apply]${NC}"
    exit 1
fi

ACTION="$1"

case $ACTION in
    "check")
        echo -e "${YELLOW}🔍 Checking Kotlin code style...${NC}"
        run_ktlint "./gradlew ktlintCheck"
        ;;
    "format")
        echo -e "${YELLOW}🎨 Formatting Kotlin code...${NC}"
        run_ktlint "./gradlew ktlintFormat"
        ;;
    "apply")
        echo -e "${YELLOW}🔍 Checking code style...${NC}"
        if run_ktlint "./gradlew ktlintCheck"; then
            echo -e "${GREEN}✅ All files pass ktlint checks!${NC}"
        else
            echo -e "${YELLOW}⚠️  Some files need formatting. Running format...${NC}"
            run_ktlint "./gradlew ktlintFormat"
            
            echo -e "${YELLOW}🔍 Re-checking after formatting...${NC}"
            run_ktlint "./gradlew ktlintCheck"
        fi
        ;;
    *)
        echo -e "${RED}Error: Invalid action '$ACTION'${NC}"
        echo -e "${CYAN}Available actions: check, format, apply${NC}"
        exit 1
        ;;
esac

echo -e "\n${CYAN}📝 Available commands:${NC}"
echo -e "${WHITE}  ./lint.sh check   - Check code style without making changes${NC}"
echo -e "${WHITE}  ./lint.sh format  - Format code to match style rules${NC}"
echo -e "${WHITE}  ./lint.sh apply   - Check and format if needed${NC}" 