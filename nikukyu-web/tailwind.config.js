/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{vue,js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                'primary': {
                    DEFAULT: '3386FF',
                    '100': '#D6E7FF',
                    '200': '#ADCFFF',
                    '300': '#85B6FF',
                    '400': '#5C9EFF',
                    '500': '#3386FF'
                },
                'secondary': {
                    DEFAULT: '#FAA201',
                    '100': '#FEECCC',
                    '200': '#FDDA99',
                    '300': '#FCC767',
                    '400': '#FBB534',
                    '500': '#FAA201'
                },
                'success': {
                    DEFAULT: '#00CE53',
                    '100': '#99ECBA',
                    '200': '#66E398',
                    '300': '#33D875',
                    '400': '#00CE53',
                },
                'warning': {
                    DEFAULT: '#FFB42B',
                    '100': '#FFE1AA',
                    '200': '#FFD280',
                    '300': '#FFC355',
                    '400': '#FFB42B',
                },
                'error': {
                    DEFAULT: '#F90C0C',
                    '100': '#FD9E9E',
                    '200': '#FD6D6D',
                    '300': '#FA3D3D',
                    '400': '#F90C0C',
                },
            }
        },
    },
    plugins: [],
}

