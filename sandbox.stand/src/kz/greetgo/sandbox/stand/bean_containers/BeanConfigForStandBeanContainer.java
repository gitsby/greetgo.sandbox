package kz.greetgo.sandbox.stand.bean_containers;

import kz.greetgo.depinject.core.BeanConfig;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.controller.controller.BeanConfigControllers;
import kz.greetgo.sandbox.stand.beans.BeanConfigStand;
import kz.greetgo.sandbox.stand.stand_register_impls.BeanConfigStandRegisters;

@BeanConfig
@Include({BeanConfigStand.class, BeanConfigStandRegisters.class, BeanConfigControllers.class})
public class BeanConfigForStandBeanContainer {}
