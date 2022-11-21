definition(
        name: "Fireplace Controller",
        namespace: "org.mynhier",
        author: "Jeremy Mynhier",
        description: "Controls Fireplace with respect to an cooling state of ACs",
        category: "My Apps",
        iconUrl: "",
        iconX2Url: ""
)

preferences {
    page(name: "mainPage")
}

Map mainPage() {
    dynamicPage(name: "mainPage", title: "Fireplace Controller", uninstall: true, install: true) {
        section {
            input "appName", "text", title: "Name this instance of Fireplace Controller", submitOnChange: true
            if (appName) app.updateLabel(appName)
            input "thermostats", "capability.thermostat", title: "Thermostats to be monitored for mode", multiple: true
            input "fireplaceSwitch", "capability.switch", title: "Fireplace Switch", multiple: false
            input "loggingEnabled", "bool", title: "Enable Logging", required: false, multiple: false, defaultValue: true, submitOnChange: true
        }
    }
}

void installed() {
    init()
}

void init() {
    unsubscribe()
    subscribe(thermostats, "thermostatOperatingState", thermostatModeHandler)
    state.anyCooling = false
    logger("Initialized", "INFO")
}

void switchHandler(evt) {
    if(state.anyCooling && fireplaceSwitch.currentSwitch == "on"){
        fireplaceSwitch.off()
        logger("Switch Turned Off", "INFO")
    } else if(fireplaceSwitch.currentSwitch == "off"){
        fireplaceSwitch.on()
        logger("Switch Turned On", "INFO")
    }
}

void thermostatModeHandler(evt){
    evaluateThermostats(evt)
}

void evaluateThermostats(evt) {
    state.anyCooling = false;
    thermostats.each {
        if(it.currentthermostatOperatingState == 'cooling') {
            state.anyCooling = true;
        }
    }
    switchHandler(evt)
}

void updated() {
    init()
}

void uninstalled() {
    state = null
    unsubscribe()
    logger("Uninstalled", "INFO")
}

void logger(msg, level){
    if(loggingEnabled){
        switch(level){
            case "INFO":
                log.info("${app.name}: ${msg}")
                break
        }
    }
}
